package com.baekho.bridgenet.domain.chain.dto.response;

import com.baekho.bridgenet.global.common.enums.ChainStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;

@Getter
@Builder
public class ChainAddResponseDTO {
    private Long chainId;
    private String chainName;
    private ChainStatus chainStatus;
    private String smartContractAddress;
    private BigInteger smartContractValue;
    private String unit;
    private BigInteger maxFeePerGas;
    private BigInteger maxPriorityFeePerGas;
    private BigInteger gasLimit;
    private Integer requiredConfirmations;
}
