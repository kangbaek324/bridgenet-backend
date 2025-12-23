package com.baekho.bridgenet.domain.bridge.dto.response;

import com.baekho.bridgenet.global.common.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class BridgeHistoryResponseDTO {
    private Long id;
    private Long fromChainId;
    private String fromChainName;
    private BigInteger fromValue;
    private String fromUnit;
    private Long toChainId;
    private String toChainName;
    private BigInteger toValue;
    private String toUnit;
    private RequestStatus status;
    private String toTransactionHash;
    private String fromTransactionHash;
    private LocalDateTime exchangedAt;
    private LocalDateTime createdAt;
}
