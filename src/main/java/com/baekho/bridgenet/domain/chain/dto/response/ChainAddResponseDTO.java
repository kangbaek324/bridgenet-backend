package com.baekho.bridgenet.domain.chain.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;

@Getter
@Builder
public class ChainAddResponseDTO {
    private Long chainId;
    private String chainName;
    private Boolean chainStatus;
    private String smartContractAddress;
    private BigInteger smartContractValue;
    private String unit;
}
