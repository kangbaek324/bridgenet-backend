package com.baekho.bridgenet.domain.bridge.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ContractBalanceGetResponseDTO {
    private BigDecimal balance;
}
