package com.baekho.bridgenet.global.common.exception;

import com.baekho.bridgenet.global.common.code.parent.ErrorCode;
import lombok.Getter;

@Getter
public class WhiteListException extends RuntimeException {
    private ErrorCode errorCode;

    public WhiteListException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
