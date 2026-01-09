package com.baekho.bridgenet.domain.bridge.dto.response;

import com.baekho.bridgenet.domain.chain.dto.response.ChainDetailApproveDTO;
import com.baekho.bridgenet.global.common.enums.RequestStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ExchangeApproveResponseDTO {
    private Long id;

    private ChainDetailApproveDTO from;
    private ChainDetailApproveDTO to;

    private RequestStatus approveStatus;
    private String transactionHash;
    private LocalDateTime approvedAt;
}
