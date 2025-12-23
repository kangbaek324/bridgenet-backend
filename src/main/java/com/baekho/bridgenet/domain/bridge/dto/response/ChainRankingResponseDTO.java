package com.baekho.bridgenet.domain.bridge.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;

@Getter
@Builder
public class ChainRankingResponseDTO {
    private int ranking;
    private Long chainId;
    private String chainName;
    private BigInteger value;   
    private String unit;
}