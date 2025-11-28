package com.baekho.bridgenet.domain.auth.dto.request;

import com.baekho.bridgenet.global.common.annotation.IsEthAddress;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class NonceRequestDTO {
    @Schema(description = "이더리움 주소 ", example = "0x...")
    @NotBlank
    @IsEthAddress
    private String address;
}
