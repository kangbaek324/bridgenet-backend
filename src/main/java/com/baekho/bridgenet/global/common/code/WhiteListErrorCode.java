package com.baekho.bridgenet.global.common.code;

import com.baekho.bridgenet.global.common.code.parent.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum WhiteListErrorCode implements ErrorCode {
    ALREADY_WHITE_LIST(HttpStatus.CONFLICT, "이미 화이트리스트입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
