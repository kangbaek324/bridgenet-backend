package com.baekho.bridgenet.global.blockchain.contract;

import com.baekho.bridgenet.domain.chain.entity.Chain;
import com.baekho.bridgenet.global.blockchain.contract.bridge.Bridge;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.StaticEIP1559GasProvider;

import java.math.BigInteger;

@Service
@AllArgsConstructor
public class SmartContractService {
    private final Credentials credentials;

    public Bridge createBridgeObject(Chain chain, Web3j web3j) {
        TransactionManager txManager = new RawTransactionManager(
                web3j,
                credentials,
                chain.getChainId()
        );

        return Bridge.load(
                chain.getSmartContractAddress(),
                web3j,
                txManager,
                new StaticEIP1559GasProvider(
                        chain.getChainId(),
                        chain.getMaxFeePerGas(),
                        chain.getMaxPriorityFeePerGas(),
                        chain.getGasLimit()
                )
        );
    }

    public void validateGasSetting(
            BigInteger maxFeePerGas,
            BigInteger maxPriorityFeePerGas,
            BigInteger gasLimit
    ) {
        if (maxFeePerGas.compareTo(maxPriorityFeePerGas) < 0) {
            throw new IllegalArgumentException("maxFeePerGas가 maxPriorityFeePerGas보다 커야합니다");
        }
    }
}
