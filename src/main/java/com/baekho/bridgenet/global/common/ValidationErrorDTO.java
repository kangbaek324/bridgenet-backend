package com.baekho.bridgenet.global.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ValidationErrorDTO {
    private String filed;
    private String message;
}
