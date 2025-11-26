package com.baekho.bridgenet.global.common.code;

import com.baekho.bridgenet.global.common.code.parent.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BlockchainErrorCode implements ErrorCode {
    ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "블록체인 요청증 실패했습니다.");
    private final HttpStatus httpStatus;
    private final String message;
}
