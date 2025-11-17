package com.baekho.bridgenet.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginRequestDTO {
    @NotBlank
    private String username;

    @NotBlank
    private String signatureData;
}
