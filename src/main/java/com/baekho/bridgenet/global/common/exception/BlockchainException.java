package com.baekho.bridgenet.global.common.exception;

import com.baekho.bridgenet.global.common.code.parent.ErrorCode;
import lombok.Getter;

@Getter
public class BlockchainException extends RuntimeException {
    private ErrorCode errorCode;

    public BlockchainException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
