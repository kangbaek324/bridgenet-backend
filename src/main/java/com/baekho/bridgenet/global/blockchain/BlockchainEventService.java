package com.baekho.bridgenet.global.blockchain;

import com.baekho.bridgenet.domain.auth.entity.Users;
import com.baekho.bridgenet.domain.auth.repository.UserRepository;
import com.baekho.bridgenet.domain.bridge.entity.ExchangeRequest;
import com.baekho.bridgenet.domain.bridge.entity.ExchangeRequestOption;
import com.baekho.bridgenet.domain.bridge.repository.ExchangeRequestOptionRepository;
import com.baekho.bridgenet.domain.bridge.repository.ExchangeRequestRepository;
import com.baekho.bridgenet.domain.chain.entity.Chain;
import com.baekho.bridgenet.domain.chain.repository.ChainRepository;
import com.baekho.bridgenet.global.blockchain.contract.bridge.Bridge;
import com.baekho.bridgenet.global.common.code.ChainErrorCode;
import com.baekho.bridgenet.global.common.enums.RequestStatus;
import com.baekho.bridgenet.global.common.exception.BlockchainException;
import com.baekho.bridgenet.global.common.exception.ChainException;
import io.reactivex.disposables.Disposable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlockchainEventService {
    private final ChainRepository chainRepository;
    private final UserRepository userRepository;
    private final ExchangeRequestOptionRepository exchangeRequestOptionRepository;
    private final ExchangeRequestRepository exchangeRequestRepository;

    private final Map<Long, List<Bridge>> bridgeMap;
    private final Map<Long, Disposable> subMap;

    /**
     * 컨트랙트의 요청을 구독합니다.
     * 항상 recoverEvent 실행 이후에 실행되어야합니다.
     * @param bridge Bridge
     * @param chain Chain
     * @param nowBlockNumber nowBlockNumber
     */
    public void subscribeToContractEvents(Bridge bridge, Chain chain, BigInteger nowBlockNumber) {
        Disposable sub = bridge.requestedEventFlowable(
                DefaultBlockParameter.valueOf(nowBlockNumber.add(BigInteger.valueOf(1))),
                DefaultBlockParameterName.LATEST
        ).subscribe(
                event -> {
                    log.info("RequestEvent: Request ID: {}", event.request.id);
                    try {
                        this.saveRequest(event, event.log.getTransactionHash());
                    } catch (Exception e) {
                        log.error("Save RequestEvent Failed: Request ID: {}, {}", event.request.id, e.getMessage(), e);
                    }
                },
                error -> {
                    log.error("[Chain: {}] Requested 이벤트 구독 에러: {}", chain.getChainName(), error.getMessage());
                    throw new IllegalStateException(error);
                }
        );

        subMap.put(chain.getChainId(), sub);
    }

    @Transactional
    public void saveRequest(Bridge.RequestedEventResponse res, String fromTransactionHash) {
        Bridge.RequestInfo request = res.request;

        Optional<Users> userOpt = userRepository.findByAddress(res.requestedBy);
        Optional<Chain> chainOpt = chainRepository.findByChainId(request.fromChainId.longValue());

        if (userOpt.isPresent() && chainOpt.isPresent()) {
            Users user = userOpt.get();
            Chain chain = chainOpt.get();

            Chain toChain = chainRepository.findByChainId(request.toChainId.longValue())
                    .orElseThrow(()-> new ChainException(ChainErrorCode.CHAIN_NOT_FOUND));
            Chain fromChain = chainRepository.findByChainId(request.fromChainId.longValue())
                    .orElseThrow(()-> new ChainException(ChainErrorCode.CHAIN_NOT_FOUND));

            ExchangeRequestOption option = exchangeRequestOptionRepository.findById(1L)
                    .orElseGet(() -> {
                        return ExchangeRequestOption.builder()
                                .id(1L)
                                .autoApprove(true)
                                .updatedUser(user)
                                .build();
                    });

            ExchangeRequest.ExchangeRequestBuilder build = ExchangeRequest.builder()
                    .idInSmartContract(request.id)
                    .toChain(toChain)
                    .toValue(request.toValue)
                    .fromChain(fromChain)
                    .fromValue(request.fromValue)
                    .user(user);

            // 처리 옵션 확인
            if (option.isAutoApprove()) {
                Bridge bridge = bridgeMap.get(toChain.getChainId()).getFirst();
                String transactionHash = null;
                boolean isBlockchainError = false;

                try {
                    TransactionReceipt receipt = bridge.triggerPayout(user.getAddress(), request.fromValue).send();
                    transactionHash = receipt.getTransactionHash();
                } catch (Exception e) {
                    log.error("[SYSTEM PROCESSING] Trigger Payout Error: {}", e.getMessage(), e);
                    isBlockchainError = true;
                }

                // 자동처리 중 오류 발생시 수동옵션으로 등록
                if (isBlockchainError) {
                    build.approveStatus(RequestStatus.PENDING);
                }
                else {
                    build.approveStatus(RequestStatus.APPROVE);
                    build.toTransactionHash(transactionHash);
                    build.approvedAt(LocalDateTime.now());
                }
            }
            else {
                build.approveStatus(RequestStatus.PENDING);
            }

            build.fromTransactionHash(fromTransactionHash);

            exchangeRequestRepository.save(build.build());

            chain.setLastBlockNumber(res.log.getBlockNumber());
            chainRepository.save(chain);

            log.info("Save RequestEvent Success: Request ID: {}", request.id);
        }
        else if (chainOpt.isEmpty()) {
            log.warn("알 수 없는 체인: {}", request.fromChainId.longValue());
        }
        else {
            log.warn("알 수 없는 주소: {}", res.requestedBy);
        }
    }
}
