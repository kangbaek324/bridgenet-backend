package com.baekho.bridgenet.domain.bridge.dto;

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
    private BigInteger fromValue;
    private Long toChainId;
    private BigInteger toValue;
    private RequestStatus status;
    private String transactionHash;
    private LocalDateTime exchangedAt;
}
