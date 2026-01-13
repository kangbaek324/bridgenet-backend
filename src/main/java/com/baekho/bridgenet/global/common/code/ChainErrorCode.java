package com.baekho.bridgenet.global.common.code;

import com.baekho.bridgenet.global.common.code.parent.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ChainErrorCode implements ErrorCode {
    ALREADY_EXIST_CHAIN(HttpStatus.CONFLICT, "이미 존재하는 체인 입니다."),
    ALREADY_EXIST_CHAIN_NAME(HttpStatus.CONFLICT, "이미 존재하는 체인 이름입니다."),
    CHAIN_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 체인입니다."),

    CHAIN_ALREADY_ACTIVATE(HttpStatus.CONFLICT, "체인이 이미 활성화 되어있습니다."),
    CHAIN_ALREADY_DEACTIVATE(HttpStatus.CONFLICT, "체인이 이미 비활성화 되어있습니다."),

    CHAIN_MUST_DEACTIVATE(HttpStatus.CONFLICT, "체인이 비활성화 되어있어야합니다."),

    RPC_NOT_CONNECTED(HttpStatus.CONFLICT, "체인에는 한 개 이상의 RPC가 연결되어 있어야 합니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
