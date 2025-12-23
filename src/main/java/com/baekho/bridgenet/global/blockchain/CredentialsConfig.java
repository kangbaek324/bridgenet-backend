package com.baekho.bridgenet.global.blockchain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.crypto.Credentials;

@Configuration
public class CredentialsConfig {
    @Bean
    public Credentials credentials(@Value("${ethereum.private.key}") String privateKey) {
        return Credentials.create(privateKey);
    }
}
