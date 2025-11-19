package com.baekho.bridgenet.global.common.exception;

import com.baekho.bridgenet.global.common.code.parent.ErrorCode;
import lombok.Getter;

@Getter
public class BridgeException extends RuntimeException {
    private ErrorCode errorCode;

    public BridgeException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
