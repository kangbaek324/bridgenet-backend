package com.baekho.bridgenet.domain.bridge.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class RequestOptionSetRequestDTO {
    @Schema(description = "true 자동, false 수동", example = "true")
    @NotNull
    private Boolean status;
}
