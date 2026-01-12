package com.baekho.bridgenet.domain.chain.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;

@Getter
@Builder
public class ChainUpdateResponseDTO {
    private Long chainId;
    private String chainName;
    private Boolean chainStatus;
    private String smartContractAddress;
    private String unit;
    private BigInteger maxFeePerGas;
    private BigInteger maxPriorityFeePerGas;
    private BigInteger gasLimit;
}
