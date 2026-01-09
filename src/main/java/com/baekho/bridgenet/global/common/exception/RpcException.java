package com.baekho.bridgenet.global.common.exception;

import com.baekho.bridgenet.global.common.code.parent.ErrorCode;
import lombok.Getter;

@Getter
public class RpcException extends RuntimeException {
    private ErrorCode errorCode;

    public RpcException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
