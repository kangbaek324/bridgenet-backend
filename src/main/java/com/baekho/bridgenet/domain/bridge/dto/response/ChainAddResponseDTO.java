package com.baekho.bridgenet.domain.bridge.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChainAddResponseDTO {
    private Long chainId;
    private String chainName;
    private String smartContractAddress;
    private Long smartContractValue;
    private String httpRpc;
    private String wsRpc;
}
