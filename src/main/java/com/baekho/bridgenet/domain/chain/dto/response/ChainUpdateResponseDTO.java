package com.baekho.bridgenet.domain.chain.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;

@Getter
@Builder
public class ChainUpdateResponseDTO {
    private Long chainId;
    private String chainName;
    private String smartContractAddress;
    private String unit;
}
