package com.baekho.bridgenet.domain.bridge.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigInteger;

@Getter
@AllArgsConstructor
public class GetChainRankingResponseDTO {
    private int ranking;
    private Long chainId;
    private String chainName;
    private BigInteger value;
}