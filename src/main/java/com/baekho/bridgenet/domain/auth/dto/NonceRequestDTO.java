package com.baekho.bridgenet.domain.auth.dto;

import com.baekho.bridgenet.global.common.annotation.IsEthAddress;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class NonceRequestDTO {
    @NotBlank
    @IsEthAddress
    private String address;
}
