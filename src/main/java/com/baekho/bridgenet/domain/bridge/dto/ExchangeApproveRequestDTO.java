package com.baekho.bridgenet.domain.bridge.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ExchangeApproveRequestDTO {
    @NotNull
    private Boolean approveStatus;
}