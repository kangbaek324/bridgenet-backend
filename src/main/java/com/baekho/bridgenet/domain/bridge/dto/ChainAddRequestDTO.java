package com.baekho.bridgenet.domain.bridge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ChainAddRequestDTO {
    @NotBlank
    private String chainName;

    @NotNull
    private Long chainId;

    @NotBlank
    private String smartContractAddress;

    @NotNull
    private Long smartContractValue;

    @NotBlank
    private String httpRpc;

    @NotBlank
    private String wsRpc;
}
