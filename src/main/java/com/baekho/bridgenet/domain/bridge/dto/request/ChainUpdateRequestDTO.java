package com.baekho.bridgenet.domain.bridge.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChainUpdateRequestDTO {
    @NotBlank
    private String chainName;

    @NotBlank
    private String smartContractAddress;

    @NotNull
    private Long smartContractValue;

    @NotBlank
    private String httpRpc;

    @NotBlank
    private String wsRpc;
}
