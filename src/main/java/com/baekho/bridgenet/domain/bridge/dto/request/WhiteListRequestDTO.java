package com.baekho.bridgenet.domain.bridge.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class WhiteListRequestDTO {
    @Schema(description = "체인 아이디", example = "")
    @NotNull
    private Long chainId;
}
