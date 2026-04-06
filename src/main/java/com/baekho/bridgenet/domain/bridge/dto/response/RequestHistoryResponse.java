package com.baekho.bridgenet.domain.bridge.dto.response;

import com.baekho.bridgenet.global.common.enums.ApproveStatus;
import com.baekho.bridgenet.global.common.enums.BridgeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class RequestHistoryResponse {
    private Long id;
    private BridgeInfo from;
    private BridgeInfo to;
    private ApproveStatus approveStatus;
    private BridgeStatus bridgeStatus;
    private LocalDateTime createdAt;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class BridgeInfo {
        private BridgeHistoryChainInfo chain;
        private List<TransactionInfo> tx;
    }
}
