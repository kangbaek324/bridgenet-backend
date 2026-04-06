package com.baekho.bridgenet.domain.bridge.dto.response;

import com.baekho.bridgenet.global.common.enums.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigInteger;

@Getter
@AllArgsConstructor
public class TransactionInfo {
    private String hash;
    private BigInteger processedBlock;
    private TransactionStatus status;
}
