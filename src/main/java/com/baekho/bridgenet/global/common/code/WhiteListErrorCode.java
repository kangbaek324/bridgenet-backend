package com.baekho.bridgenet.global.common.code;

import com.baekho.bridgenet.global.common.code.parent.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum WhiteListErrorCode implements ErrorCode {
    UNKNOWN_CHAIN_ID(HttpStatus.BAD_REQUEST, "지원하지 않는 체인입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
