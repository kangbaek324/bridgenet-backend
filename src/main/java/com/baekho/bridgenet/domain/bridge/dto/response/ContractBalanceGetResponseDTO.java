package com.baekho.bridgenet.domain.bridge.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigInteger;

@Getter
@AllArgsConstructor
public class ContractBalanceGetResponseDTO {
    private BigInteger balance;
    private String unit;
}
