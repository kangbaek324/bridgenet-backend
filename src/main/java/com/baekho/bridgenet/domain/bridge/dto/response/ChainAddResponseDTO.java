package com.baekho.bridgenet.domain.bridge.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigInteger;

@Getter
@AllArgsConstructor
public class ChainAddResponseDTO {
    private Long chainId;
    private String chainName;
    private String smartContractAddress;
    private BigInteger smartContractValue;
    private String unit;
    private String httpRpc;
    private String wsRpc;
}
