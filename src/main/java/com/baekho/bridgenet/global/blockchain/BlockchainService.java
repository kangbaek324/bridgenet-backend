package com.baekho.bridgenet.global.blockchain;

import com.baekho.bridgenet.domain.auth.entity.Users;
import com.baekho.bridgenet.domain.auth.repository.UserRepository;
import com.baekho.bridgenet.domain.chain.entity.Chain;
import com.baekho.bridgenet.domain.bridge.entity.ExchangeRequest;
import com.baekho.bridgenet.domain.bridge.entity.ExchangeRequestOption;
import com.baekho.bridgenet.domain.chain.entity.Rpc;
import com.baekho.bridgenet.domain.chain.repository.ChainRepository;
import com.baekho.bridgenet.domain.bridge.repository.ExchangeRequestOptionRepository;
import com.baekho.bridgenet.domain.bridge.repository.ExchangeRequestRepository;
import com.baekho.bridgenet.domain.chain.repository.RpcRepository;
import com.baekho.bridgenet.domain.chain.service.RpcService;
import com.baekho.bridgenet.global.blockchain.contract.bridge.Bridge;
import com.baekho.bridgenet.global.common.code.ChainErrorCode;
import com.baekho.bridgenet.global.common.enums.Protocol;
import com.baekho.bridgenet.global.common.enums.RequestStatus;
import com.baekho.bridgenet.global.common.exception.ChainException;
import io.reactivex.disposables.Disposable;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.abi.EventEncoder;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.StaticEIP1559GasProvider;

import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlockchainService {
    private final ChainRepository chainRepository;
    private final UserRepository userRepository;
    private final RpcRepository rpcRepository;
    private final ExchangeRequestOptionRepository exchangeRequestOptionRepository;
    private final ExchangeRequestRepository exchangeRequestRepository;

    private final RpcState rpcState;

    private final Map<Long, List<Bridge>> bridgeMap;
    private final Map<Long, List<Web3j>> httpWeb3jMap;
    private final Map<Long, Disposable> subMap;

    private final Credentials credentials;

    private boolean isRecover = true;

    @PostConstruct
    public void init() throws IOException {
        List<Chain> chains = chainRepository.findAllByStatus(true);

        // Bridge 컨트랙트 인스턴스 Map 에 추가
        for (Chain chain : chains) {
            List<Rpc> rpcs = rpcRepository.findAllByChainAndProtocol(chain, Protocol.HTTP);

            // RPC 등록
            for (Rpc rpc : rpcs) {
                createHttpRpc(chain, rpc);
            }

            // 누락 이벤트 복구 및 이벤트 리스너 등록
            Long chainId = chain.getChainId();

            Bridge bridge = bridgeMap.get(chainId).get(rpcState.rpcCount(chainId));
            Web3j httpWeb3 = httpWeb3jMap.get(chainId).get(rpcState.rpcCount(chainId));
            BigInteger nowBlockNumber = httpWeb3.ethBlockNumber().send().getBlockNumber();

            subscribeToContractEvents(bridge, chain, nowBlockNumber);

            try {
                recoverEvent(chain, nowBlockNumber);

                isRecover = false;
            } catch (Exception e) {
                log.error("Recover Event Error: {}", e.getMessage(), e);
            }
        }
    }

    public void subscribeToContractEvents(Bridge bridge, Chain chain, BigInteger nowBlockNumber) {
        Queue<Bridge.RequestedEventResponse> queue = new ArrayDeque<>();

        Disposable sub = bridge.requestedEventFlowable(
                DefaultBlockParameter.valueOf(nowBlockNumber),
                DefaultBlockParameterName.LATEST
        ).subscribe(
                event -> {
                    queue.offer(event);
                    if (!isRecover) {
                        while (!queue.isEmpty()) {
                            event = queue.poll();
                            log.info("RequestEvent: Request ID: {}", event.request.id);
                            try {
                                this.saveRequest(event, event.log.getTransactionHash());
                            } catch (Exception e) {
                                log.error("Save RequestEvent Failed: Request ID: {}, {}", event.request.id, e.getMessage(), e);
                            }
                        }
                    }
                },
                error -> {
                    log.error("[Chain: {}] Requested 이벤트 구독 에러: {}", chain.getChainName(), error.getMessage());
                }
        );

        subMap.put(chain.getChainId(), sub);
    }

    /**
     * * @TODO 등록시 블록체인에서 이미 처리된 요청인지 확인하는 로직 추가 필요
     * * 나중에 DB 날아갔을때 recover 함수를 실행시키면 미처리 요청으로 들어가기 때문
     **/
    private void recoverEvent(Chain chain, BigInteger nowBlockNumber) throws IOException, InterruptedException {
        BigInteger lastBlockNumber = chain.getLastBlockNumber();

        // 맨처음 복구를 시작한 블록
        BigInteger recoverStartBlock = lastBlockNumber.add(BigInteger.ONE);

        long recoverValue = 1500;

        // 매 시도마다 블록 시작값과 마지막 블록값
        BigInteger startBlockNumber = lastBlockNumber.add(BigInteger.valueOf(1));
        BigInteger finishBlockNumber = startBlockNumber.add(BigInteger.valueOf(recoverValue));

        log.info("---- Start Recover Requested Event ChainId: {} ---\n", chain.getChainId());
        long start = System.currentTimeMillis();

        boolean isFinish = false;
        long chainId = chain.getChainId();

        while (true) {
            Bridge bridge = bridgeMap.get(chainId).get(rpcState.rpcCount(chainId));
            Web3j httpWeb3 = httpWeb3jMap.get(chainId).get(rpcState.rpcCount(chainId));

            showPercentLog(chain, recoverStartBlock, nowBlockNumber, finishBlockNumber);

            if (finishBlockNumber.compareTo(nowBlockNumber) > 0) {
                finishBlockNumber = nowBlockNumber;
                isFinish = true;

                System.out.println("\n");
            }

            EthFilter filter = new EthFilter(
                    DefaultBlockParameter.valueOf(startBlockNumber),
                    DefaultBlockParameter.valueOf(finishBlockNumber),
                    bridge.getContractAddress()
            );

            filter.addSingleTopic(EventEncoder.encode(Bridge.REQUESTED_EVENT));
            EthLog ethLogs = httpWeb3.ethGetLogs(filter).send();

            for (EthLog.LogResult logResult : ethLogs.getLogs()) {
                Log bcLog = (Log) logResult.get();
                Bridge.RequestedEventResponse e = Bridge.getRequestedEventFromLog(bcLog);

                saveRequest(e, e.log.getTransactionHash());
            }

            if (isFinish) {
                break;
            }
            else {
                startBlockNumber = finishBlockNumber.add(BigInteger.valueOf(1));
                finishBlockNumber = startBlockNumber.add(BigInteger.valueOf(recoverValue));

                // RPC 429 (To many Request) 해결
                Thread.sleep(200);
            }
        }

        chain.setLastBlockNumber(finishBlockNumber);
        chainRepository.save(chain);

        long end = System.currentTimeMillis();
        log.info("---- Success Recover Requested Event ----");
        System.out.println("Time Taken: " + (end - start) + "ms");
    }

    private static void showPercentLog(
            Chain chain,
            BigInteger recoverStartBlock,
            BigInteger recoverEndBlockNumber,
            BigInteger nowRecoverBlockNumber
    ) {
        BigInteger total = recoverEndBlockNumber.subtract(recoverStartBlock);
        BigInteger progressed = nowRecoverBlockNumber.subtract(recoverStartBlock);

        double percent;
        if (total.signum() <= 0) {
            percent = 100.0;
        } else {
            percent = progressed
                    .max(BigInteger.ZERO)
                    .min(total)
                    .multiply(BigInteger.valueOf(100))
                    .doubleValue() / total.doubleValue();
        }

        System.out.printf(
                "\r[Recovering %s] Now: %s | End: %s (%.2f%%)",
                chain.getChainName(),
                nowRecoverBlockNumber.toString(),
                recoverEndBlockNumber.toString(),
                percent
        );
    }

    @Transactional
    private void saveRequest(Bridge.RequestedEventResponse res, String fromTransactionHash) {
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

    public Bridge createBridgeObject(Chain chain, Web3j web3j) {
        // @TODO
        // 메서드와 약간 의미가 맞지 않는듯 수정 필요
        TransactionManager txManager = new RawTransactionManager(
                web3j,
                credentials,
                chain.getChainId()
        );

        // @TODO 체인별 가스 저장 필요
        return Bridge.load(
                chain.getSmartContractAddress(),
                web3j,
                txManager,
                new StaticEIP1559GasProvider(
                        chain.getChainId(),
                        BigInteger.valueOf(50_000_000_000L),
                        BigInteger.valueOf(25_000_000_000L),
                        BigInteger.valueOf(150000)
                )
        );
    }

    // @TODO RPC Service로 분리해야됨 순환 참조 문제 때문에 임시로 이동
    /**
     * 새로운 HTTP RPC를 등록합니다.
     * @param chain
     * Chain 객체
     * @param rpc
     * Rpc 객체
     */
    public void createHttpRpc(Chain chain, Rpc rpc) {
        Web3j web3j = Web3j.build(new HttpService(rpc.getUrl()));

        httpWeb3jMap
                .computeIfAbsent(chain.getChainId(), k -> new ArrayList<>())
                .add(web3j);

        Bridge bridge = createBridgeObject(chain, web3j);
        bridgeMap
                .computeIfAbsent(chain.getChainId(), k -> new ArrayList<>())
                .add(bridge);
    }
}
