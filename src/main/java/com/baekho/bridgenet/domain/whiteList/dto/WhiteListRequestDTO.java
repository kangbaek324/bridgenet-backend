package com.baekho.bridgenet.domain.whiteList.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class WhiteListRequestDTO {
    @NotNull
    private Long networkId;
}
