package com.baekho.bridgenet.global.common.exception;

import com.baekho.bridgenet.global.common.code.parent.ErrorCode;
import lombok.Getter;

@Getter
public class AuthException extends RuntimeException {
    private ErrorCode errorCode;

    public AuthException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
