package com.baekho.bridgenet.global.blockchain;

import com.baekho.bridgenet.global.blockchain.contract.bridge.Bridge;
import io.reactivex.disposables.Disposable;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;

import java.util.*;

@Configuration
@RequiredArgsConstructor
public class BlockchainConfig {
    private final Map<Long, List<Bridge>> bridgeMap = new HashMap<>();
    private final Map<Long, List<Web3j>> httpWeb3jMap = new HashMap<>();
    private final Map<Long, Disposable> subMap = new HashMap<>();

    @Bean
    public Map<Long, List<Bridge>> bridgeMap() {
        return bridgeMap;
    }

    @Bean
    public Map<Long, List<Web3j>> httpWeb3jMap() { return httpWeb3jMap; }

    @Bean
    public Map<Long, Disposable> subMap() { return subMap; }
}
