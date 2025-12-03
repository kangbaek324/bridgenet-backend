package com.baekho.bridgenet.global.config;

import com.baekho.bridgenet.global.contract.bridge.Bridge;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;

import java.util.*;

@Configuration
@RequiredArgsConstructor
public class BlockchainConfig {
    private final Map<Long, Bridge> bridgeMap = new HashMap<>();
    private final Map<Long, Web3j> httpWeb3jMap = new HashMap<>();

    @Bean
    public Map<Long, Bridge> bridgeMap() {
        return bridgeMap;
    }

    @Bean
    public Map<Long, Web3j> httpWeb3jMap() { return httpWeb3jMap; }
}
