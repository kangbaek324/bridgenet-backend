package com.baekho.bridgenet.domain.bridge.dto.response;

import com.baekho.bridgenet.global.common.enums.RequestStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BridgeHistoryResponseDTO {
    private Long id;

    private ChainDetailBridgeHistoryDTO from;
    private ChainDetailBridgeHistoryDTO to;

    private RequestStatus status;
    private LocalDateTime exchangedAt;
    private LocalDateTime createdAt;
}
