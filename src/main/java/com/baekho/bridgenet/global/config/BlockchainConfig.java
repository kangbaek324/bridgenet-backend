package com.baekho.bridgenet.global.config;

import com.baekho.bridgenet.domain.bridge.entity.Chains;
import com.baekho.bridgenet.domain.bridge.repository.ChainsRepository;
import com.baekho.bridgenet.domain.bridge.service.BridgeService;
import com.baekho.bridgenet.global.blockchain.bridgenet.Bridge;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.websocket.WebSocketService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.StaticEIP1559GasProvider;

import java.math.BigInteger;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class BlockchainConfig {
    private final ChainsRepository chainsRepository;
    private final BridgeService bridgeService;
    private final Map<Long, Bridge> bridgeMap = new HashMap<>();
    private final Map<Long, Web3j> web3jMap = new HashMap<>();
    private final Credentials credentials;

    @Bean
    public Map<Long, Bridge> bridgeMap() {
        return bridgeMap;
    }

    @Bean
    public Map<Long, Web3j> web3jMap() { return web3jMap; }

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
            web3jMap.put(chain.getChainId(), web3j);

            subscribeToContractEvents(bridge, chain);

            log.info("WebSocket 연결 성공 - Chain: {}", chain.getChainName());
        } catch (ConnectException e) {
            log.error("WebSocket Connect Error: {}", e);
        } catch (Exception e) {
            log.error("Error: {}", e);
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
                        bridgeService.saveRequest(event);
                    } catch (Exception e) {
                        log.error("Save RequestEvent Failed: Request ID: {}, {}", event.request.id, e);
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
}
