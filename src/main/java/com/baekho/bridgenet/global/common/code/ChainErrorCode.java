package com.baekho.bridgenet.global.common.code;

import com.baekho.bridgenet.global.common.code.parent.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ChainErrorCode implements ErrorCode {
    ALREADY_EXIST_CHAIN_ID(HttpStatus.CONFLICT, "이미 추가된 체인 입니다."),
    CHAIN_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 체인입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
