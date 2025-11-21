package com.baekho.bridgenet.global.config;

import com.baekho.bridgenet.domain.bridge.entity.Chains;
import com.baekho.bridgenet.domain.bridge.repository.ChainsRepository;
import com.baekho.bridgenet.global.blockchain.bridgenet.Bridge;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.StaticEIP1559GasProvider;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class BlockchainConfig {
    private final ChainsRepository chainsRepository;
    private final Map<Long, Bridge> bridgeMap = new HashMap<>();
    private final Credentials credentials;

    @Bean
    public Map<Long, Bridge> bridgeMap() {
        return bridgeMap;
    }

    @PostConstruct
    public void init() {
        List<Chains> chains = chainsRepository.findAll();

        for (Chains chain : chains) {
            Bridge bridge = createBridgeObject(chain);
            bridgeMap.put(chain.getChainId(), bridge);
        }
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
