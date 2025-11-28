package com.baekho.bridgenet.domain.bridge.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class AddContractBalanceRequestDTO {
    @Schema(description = "충전할 코인의 양", example = "0.5ETH -> 0.5")
    @NotNull
    private BigDecimal balance;
}
