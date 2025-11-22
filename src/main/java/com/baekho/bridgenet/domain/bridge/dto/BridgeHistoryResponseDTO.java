package com.baekho.bridgenet.domain.bridge.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class BridgeHistoryResponseDTO {
    private Long id;
    private Long fromChainId;
    private String fromValue;
    private Long toChainId;
    private String toValue;
    private LocalDateTime exchangedAt;
}
