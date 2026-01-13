package com.baekho.bridgenet.domain.chain.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;

@Getter
@Builder
public class AdminChainDetailDTO {
    private Long chainId;
    private String chainName;
    private String smartContractAddress;
    private BigInteger smartContractValue;
    private String unit;
    private boolean status;
    private BigInteger maxFeePerGas;
    private BigInteger maxPriorityFeePerGas;
    private BigInteger gasLimit;
}
