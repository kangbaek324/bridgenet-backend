package com.baekho.bridgenet.domain.bridge.dto;

import com.baekho.bridgenet.global.common.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ExchangeApproveResponseDTO {
    private Long id;
    private Long fromChainId;
    private BigInteger fromValue;
    private Long toChainId;
    private BigInteger toValue;
    private RequestStatus approveStatus;
    private String transactionHash;
    private LocalDateTime approvedAt;
}
