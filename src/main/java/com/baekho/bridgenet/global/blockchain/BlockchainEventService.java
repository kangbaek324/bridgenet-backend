package com.baekho.bridgenet.global.blockchain;

import com.baekho.bridgenet.domain.auth.entity.User;
import com.baekho.bridgenet.domain.auth.repository.UserRepository;
import com.baekho.bridgenet.domain.bridge.entity.BridgeTransaction;
import com.baekho.bridgenet.domain.bridge.entity.ExchangeRequest;
import com.baekho.bridgenet.domain.bridge.entity.ExchangeRequestOption;
import com.baekho.bridgenet.domain.bridge.repository.BridgeTransactionRepository;
import com.baekho.bridgenet.domain.bridge.repository.ExchangeRequestOptionRepository;
import com.baekho.bridgenet.domain.bridge.repository.ExchangeRequestRepository;
import com.baekho.bridgenet.domain.chain.entity.Chain;
import com.baekho.bridgenet.domain.chain.repository.ChainRepository;
import com.baekho.bridgenet.global.blockchain.contract.bridge.Bridge;
import com.baekho.bridgenet.global.common.code.ChainErrorCode;
import com.baekho.bridgenet.global.common.enums.ApproveStatus;
import com.baekho.bridgenet.global.common.enums.BridgeStatus;
import com.baekho.bridgenet.global.common.enums.TransactionStatus;
import com.baekho.bridgenet.global.common.enums.TransactionType;
import com.baekho.bridgenet.global.common.exception.ChainException;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.abi.EventEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.Log;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlockchainEventService {
    private final ChainRepository chainRepository;
    private final UserRepository userRepository;
    private final ExchangeRequestOptionRepository exchangeRequestOptionRepository;
    private final ExchangeRequestRepository exchangeRequestRepository;
    private final BridgeTransactionRepository bridgeTransactionRepository;

    private final Map<Long, List<Web3j>> httpWeb3jMap;
    private final Map<Long, Disposable> subMap;
    private final RpcState rpcState;

    /**
     * 컨트랙트의 요청을 구독합니다.
     * 항상 recoverEvent 실행 이후에 실행되어야합니다.
     * @param bridge Bridge
     * @param chain Chain
     * @param nowBlockNumber nowBlockNumber
     */
    public void subscribeToContractEvents(Bridge bridge, Chain chain, BigInteger nowBlockNumber) {
        Long chainId = chain.getChainId();
        AtomicReference<BigInteger> lastBlock = new AtomicReference<>(nowBlockNumber);

        Disposable sub = Flowable.interval(5, TimeUnit.SECONDS)
                .subscribe(
                        tick -> {
                            try {
                                Web3j web3j = httpWeb3jMap.get(chainId).get(rpcState.rpcCount(chainId));
                                BigInteger fromBlock = lastBlock.get().add(BigInteger.ONE);
                                BigInteger toBlock = web3j.ethBlockNumber().send().getBlockNumber();

                                if (fromBlock.compareTo(toBlock) > 0) return;

                                EthFilter filter = new EthFilter(
                                        DefaultBlockParameter.valueOf(fromBlock),
                                        DefaultBlockParameter.valueOf(toBlock),
                                        bridge.getContractAddress()
                                );
                                filter.addSingleTopic(EventEncoder.encode(Bridge.REQUESTED_EVENT));

                                EthLog ethLogs = web3j.ethGetLogs(filter).send();

                                if (ethLogs.hasError()) {
                                    log.error("[Chain: {}] ethGetLogs 에러: {}", chain.getChainName(), ethLogs.getError().getMessage());
                                    return;
                                }

                                List<EthLog.LogResult> logs = ethLogs.getLogs();
                                if (logs == null) return;

                                for (EthLog.LogResult<?> logResult : logs) {
                                    Bridge.RequestedEventResponse event = Bridge.getRequestedEventFromLog((Log) logResult.get());
                                    log.info("RequestEvent: Request ID: {}", event.request.id);
                                    try {
                                        this.saveRequest(event);
                                    } catch (Exception e) {
                                        log.error("Save RequestEvent Failed: Request ID: {}, {}", event.request.id, e.getMessage(), e);
                                    }
                                }

                                lastBlock.set(toBlock);
                            } catch (Exception e) {
                                log.error("[Chain: {}] Requested 이벤트 폴링 에러: {}", chain.getChainName(), e.getMessage(), e);
                            }
                        },
                        error -> log.error("[Chain: {}] Requested 이벤트 구독 에러: {}", chain.getChainName(), error.getMessage(), error)
                );

        subMap.put(chainId, sub);
    }

    @Transactional
    public void saveRequest(Bridge.RequestedEventResponse e) {
        Bridge.RequestInfo request = e.request;
        String txHash = e.log.getTransactionHash();
        BigInteger blockNumber = e.log.getBlockNumber();

        Optional<User> userOpt = userRepository.findByAddress(e.requestAddress);
        Optional<Chain> chainOpt = chainRepository.findByChainId(request.fromChainId.longValue());

        if (userOpt.isPresent() && chainOpt.isPresent()) {
            User user = userOpt.get();
            Chain chain = chainOpt.get();

            Chain toChain = chainRepository.findByChainId(request.toChainId.longValue())
                    .orElseThrow(()-> new ChainException(ChainErrorCode.CHAIN_NOT_FOUND));
            Chain fromChain = chainRepository.findByChainId(request.fromChainId.longValue())
                    .orElseThrow(()-> new ChainException(ChainErrorCode.CHAIN_NOT_FOUND));

            // 브릿징 요청 설정이 없을 경우 생성
            ExchangeRequestOption option = exchangeRequestOptionRepository.findById(1L)
                    .orElseGet(() -> {
                        return ExchangeRequestOption.builder()
                                .id(1L)
                                .autoApprove(true)
                                .updatedUser(user)
                                .build();
                    });
            exchangeRequestOptionRepository.save(option);

            // 요청 레코드 생성
            ExchangeRequest reqBuild = ExchangeRequest.builder()
                    .idInSmartContract(request.id)
                    .toChain(toChain)
                    .toValue(request.toValue)
                    .fromChain(fromChain)
                    .fromValue(request.fromValue)
                    .user(user)
                    .approveStatus(option.isAutoApprove() ? ApproveStatus.APPROVE : ApproveStatus.PENDING)
                    .approvedAt(option.isAutoApprove() ? LocalDateTime.now() : null)
                    .bridgeStatus(BridgeStatus.PENDING)
                    .build();

            // 트랜잭션 등록
            BridgeTransaction txBuild = BridgeTransaction
                    .builder()
                    .exchangeRequest(reqBuild)
                    .chain(chain)
                    .transactionHash(txHash)
                    .type(TransactionType.FROM)
                    .status(TransactionStatus.PENDING)
                    .processedBlock(blockNumber)
                    .build();

            exchangeRequestRepository.save(reqBuild);
            bridgeTransactionRepository.save(txBuild);

            chain.setLastBlockNumber(blockNumber);
            chainRepository.save(chain);

            log.info("Save RequestEvent Success: Request ID: {}", request.id);
        }
        else if (chainOpt.isEmpty()) {
            log.warn("알 수 없는 체인: {}", request.fromChainId.longValue());
        }
        else {
            log.warn("알 수 없는 주소: {}", e.requestAddress);
        }
    }
}
