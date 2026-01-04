package com.baekho.bridgenet.global.common.code;

import com.baekho.bridgenet.global.common.code.parent.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RpcErrorCode implements ErrorCode {
    RPC_NOT_FOUND(HttpStatus.NOT_FOUND, "RPC가 존재하지 않습니다."),
    INVALID_RPC(HttpStatus.SERVICE_UNAVAILABLE, "RPC 연결에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
