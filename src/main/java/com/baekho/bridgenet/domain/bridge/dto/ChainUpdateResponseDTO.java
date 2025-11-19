package com.baekho.bridgenet.domain.bridge.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChainUpdateResponseDTO {
    private Long id;
    private Long chainId;
    private String chainName;
    private String smartContractAddress;
    private Long smartContractValue;
    private String httpRpc;
    private String wsRpc;
}
