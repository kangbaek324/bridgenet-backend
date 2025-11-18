package com.baekho.bridgenet.global.config;

import com.baekho.bridgenet.global.blockchain.bridgenet.Bridge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.gas.StaticEIP1559GasProvider;

import java.math.BigInteger;

@Configuration
public class BlockchainConfig {

    @Value("${ethereum.sepolia.http.url}")
    private String sepoliaHttpUrl;

    @Value("${ethereum.sepolia.ws.url}")
    private String sepoliaWsUrl;

    @Value("${ethereum.sepolia.contract.address}")
    private String sepoliaContractAddress;

    @Value("${ethereum.amoy.http.url}")
    private String amoyHttpUrl;

    @Value("${ethereum.amoy.ws.url}")
    private String amoyWsUrl;

    @Value("${ethereum.amoy.contract.address}")
    private String amowyContractAddress;

    @Value("${ethereum.private.key}")
    private String privateKey;

    @Bean
    public Credentials credentials() {
        return Credentials.create(privateKey);
    }

    // Sepolia
    @Bean
    public Web3j sepoliaWeb3jHttp() {
        return Web3j.build(new HttpService(sepoliaHttpUrl));
    }

//    @Bean
//    public Web3j sepoliaWeb3jWs() throws ConnectException {
//        WebSocketService wsService = new WebSocketService(sepoliaWsUrl, true);
//        wsService.connect();
//        return Web3j.build(wsService);
//    }

    @Bean
    public Bridge sepoliaBridge(Web3j sepoliaWeb3jHttp, Credentials credentials) {
        TransactionManager txManager = new RawTransactionManager(
                sepoliaWeb3jHttp,
                credentials,
                11155111L // Sepolia chainId
        );

        return Bridge.load(
                sepoliaContractAddress,
                sepoliaWeb3jHttp,
                txManager,
                new DefaultGasProvider()
        );
    }

    // Amoy
    @Bean
    public Web3j amoyWeb3jHttp() {
        return Web3j.build(new HttpService(amoyHttpUrl));
    }

//    @Bean
//    public Web3j amoyWeb3jWs() throws ConnectException {
//        WebSocketService wsService = new WebSocketService(amoyWsUrl, true);
//        wsService.connect();
//        return Web3j.build(wsService);
//    }

    @Bean
    public Bridge amoyBridge(Web3j amoyWeb3jHttp, Credentials credentials) {
        TransactionManager txManager = new RawTransactionManager(
                amoyWeb3jHttp,
                credentials,
                80002L
        );

        return Bridge.load(
                amowyContractAddress,
                amoyWeb3jHttp,
                txManager,
                new StaticEIP1559GasProvider(
                    80002L,
                        BigInteger.valueOf(50_000_000_000L),
                        BigInteger.valueOf(25_000_000_000L),
                        BigInteger.valueOf(150000)
                )
        );
    }
}
