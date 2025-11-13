package com.baekho.bridgenet.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class RegisterRequestDTO {
    @NotBlank
    private String username;

    @NotBlank
    private String address;

    @NotBlank
    private String signatureData;
}
