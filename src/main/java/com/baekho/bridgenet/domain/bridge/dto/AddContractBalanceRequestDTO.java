package com.baekho.bridgenet.domain.bridge.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class AddContractBalanceRequestDTO {
    @NotNull
    private BigInteger balance;
}
