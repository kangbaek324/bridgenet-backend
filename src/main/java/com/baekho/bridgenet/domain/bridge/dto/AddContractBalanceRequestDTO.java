package com.baekho.bridgenet.domain.bridge.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class AddContractBalanceRequestDTO {
    @NotNull
    private BigDecimal balance;
}
