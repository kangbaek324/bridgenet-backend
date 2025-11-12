package com.baekho.bridgenet.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NonceResponseDTO {
    private String nonce;
}
