package com.baekho.bridgenet.domain.bridge.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class WhiteListRequestDTO {
    @NotNull
    private Long networkId;
}
