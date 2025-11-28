package com.baekho.bridgenet.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class RegisterRequestDTO {
    @Schema(description = "유저 이름", example = "baekho")
    @NotBlank
    private String username;

    @Schema(description = "이더리움 주소", example = "0x...")
    @NotBlank
    private String address;

    @Schema(description = "서명 메세지를 서명한 값", example = "")
    @NotBlank
    private String signatureData;
}
