package com.baekho.bridgenet.domain.bridge.dto.response;

import com.baekho.bridgenet.global.common.enums.ApproveStatus;
import com.baekho.bridgenet.global.common.enums.BridgeStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BridgeHistoryResponseDTO {
    private Long id;
    private BridgeHistoryChainInfo from;
    private BridgeHistoryChainInfo to;
    private ApproveStatus approveStatus;
    private BridgeStatus bridgeStatus;
    private LocalDateTime createdAt;
}
