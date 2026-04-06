package com.baekho.bridgenet.global.common.code;

import com.baekho.bridgenet.global.common.code.parent.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BridgeErrorCode implements ErrorCode {
    REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 requestId 입니다."),
    REQUEST_ALREADY_PROCESSED(HttpStatus.CONFLICT, "이미 처리된 요청입니다.");
    private final HttpStatus httpStatus;
    private final String message;
}
