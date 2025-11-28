package com.baekho.bridgenet.domain.bridge.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ExchangeApproveRequestDTO {
    @Schema(description = "승인시 APPROVE, 거부시 REJECT", example = "APPROVE")
    @NotNull
    private Boolean approveStatus;
}