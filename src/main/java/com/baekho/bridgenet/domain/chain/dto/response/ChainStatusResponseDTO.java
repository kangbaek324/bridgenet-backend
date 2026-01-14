package com.baekho.bridgenet.domain.chain.dto.response;

import com.baekho.bridgenet.global.common.enums.ChainStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChainStatusResponseDTO {
    private ChainStatus status;
}
