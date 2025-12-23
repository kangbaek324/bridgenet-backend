package com.baekho.bridgenet.domain.bridge.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;

@Getter
@Builder
public class ChainUpdateResponseDTO {
    private Long chainId;
    private String chainName;
    private String smartContractAddress;
    private BigInteger smartContractValue;
    private String unit;
    private String httpRpc;
    private String wsRpc;
}
