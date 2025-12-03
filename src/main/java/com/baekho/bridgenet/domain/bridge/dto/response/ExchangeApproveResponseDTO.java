package com.baekho.bridgenet.domain.bridge.dto.response;

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
    private String fromUnit;
    private Long toChainId;
    private BigInteger toValue;
    private String toUnit;
    private RequestStatus approveStatus;
    private String transactionHash;
    private LocalDateTime approvedAt;
}
