package com.baekho.bridgenet.domain.chain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigInteger;

@Getter
@AllArgsConstructor
public class ContractBalanceGetResponseDTO {
    private BigInteger balance;
    private String unit;
}
