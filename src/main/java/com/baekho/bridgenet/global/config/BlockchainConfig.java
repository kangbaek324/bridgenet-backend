package com.baekho.bridgenet.global.config;

import com.baekho.bridgenet.domain.auth.entity.Users;
import com.baekho.bridgenet.domain.auth.repository.UserRepository;
import com.baekho.bridgenet.domain.bridge.entity.Chains;
import com.baekho.bridgenet.domain.bridge.entity.ExchangeRequest;
import com.baekho.bridgenet.domain.bridge.entity.ExchangeRequestOption;
import com.baekho.bridgenet.domain.bridge.repository.ChainsRepository;
import com.baekho.bridgenet.domain.bridge.repository.ExchangeRequestOptionRepository;
import com.baekho.bridgenet.domain.bridge.repository.ExchangeRequestRepository;
import com.baekho.bridgenet.global.common.code.ChainErrorCode;
import com.baekho.bridgenet.global.common.enums.RequestStatus;
import com.baekho.bridgenet.global.common.exception.ChainException;
import com.baekho.bridgenet.global.contract.bridge.Bridge;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.websocket.WebSocketService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.StaticEIP1559GasProvider;

import java.math.BigInteger;
import java.net.ConnectException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class BlockchainConfig {
    private final ChainsRepository chainsRepository;
    private final UserRepository userRepository;
    private final ExchangeRequestOptionRepository exchangeRequestOptionRepository;
    private final ExchangeRequestRepository exchangeRequestRepository;
    private final Map<Long, Bridge> bridgeMap = new HashMap<>();
    private final Map<Long, Web3j> wsWeb3jMap = new HashMap<>();
    private final Map<Long, Web3j> httpWeb3jMap = new HashMap<>();
    private final Credentials credentials;

    @Bean
    public Map<Long, Bridge> bridgeMap() {
        return bridgeMap;
    }

    @Bean
    public Map<Long, Web3j> wsWeb3jMap() { return wsWeb3jMap; }

    @Bean
    public Map<Long, Web3j> httpWeb3jMap() { return httpWeb3jMap; }

    @PostConstruct
    public void init() {
        List<Chains> chains = chainsRepository.findAll();

        for (Chains chain : chains) {
            Bridge bridge = createBridgeObject(chain);
            bridgeMap.put(chain.getChainId(), bridge);

            setWebSocket(bridge, chain);
        }
    }

    private void setWebSocket(Bridge bridge, Chains chain) {
        try {
            WebSocketService ws = new WebSocketService(
                    chain.getWsRpc(),
                    true
            );

            ws.connect();

            Web3j web3j = Web3j.build(ws);
            wsWeb3jMap.put(chain.getChainId(), web3j);

            subscribeToContractEvents(bridge, chain);

            log.info("WebSocket 연결 성공 - Chain: {}", chain.getChainName());
        } catch (ConnectException e) {
            log.error("WebSocket Connect Error: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e);
        }
    }

    private void subscribeToContractEvents(Bridge bridge, Chains chain) {
        bridge.requestedEventFlowable(
                DefaultBlockParameterName.LATEST,
                DefaultBlockParameterName.LATEST
        ).subscribe(
                event -> {
                    log.info("RequestEvent: Request ID: {}", event.request.id);
                    try {
                        this.saveRequest(event);
                    } catch (Exception e) {
                        log.error("Save RequestEvent Failed: Request ID: {}, {}", event.request.id, e.getMessage(), e);
                    }
                },
                error -> {
                    log.error("[Chain: {}] Requested 이벤트 구독 에러: {}",
                            chain.getChainName(), error.getMessage());
                }
        );
    }

    public Bridge createBridgeObject(Chains chain) {
        Web3j web3j = Web3j.build(new HttpService(chain.getHttpRpc()));
        httpWeb3jMap.put(chain.getChainId(), web3j);

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

    @Transactional
    private void saveRequest(Bridge.RequestedEventResponse res) {
        Bridge.RequestInfo request = res.request;

        Optional<Users> userOpt = userRepository.findByAddress(res.requestedBy);
        Optional<Chains> chainOpt = chainsRepository.findByChainId(request.fromChainId.longValue());

        if (userOpt.isPresent() && chainOpt.isPresent()) {
            Users user = userOpt.get();
            Chains chain = chainOpt.get();

            Chains toChain = chainsRepository.findByChainId(request.toChainId.longValue())
                    .orElseThrow(()-> new ChainException(ChainErrorCode.CHAIN_NOT_FOUND));
            Chains fromChain = chainsRepository.findByChainId(request.fromChainId.longValue())
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
                Bridge bridge = bridgeMap.get(toChain.getChainId());
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
                    build.transactionHash(transactionHash);
                    build.approvedAt(LocalDateTime.now());
                }
            }
            else {
                build.approveStatus(RequestStatus.PENDING);
            }

            exchangeRequestRepository.save(build.build());

            chain.setLastBlockNumber(res.log.getBlockNumber());

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
