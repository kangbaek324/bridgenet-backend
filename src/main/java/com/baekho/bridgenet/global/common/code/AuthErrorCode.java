package com.baekho.bridgenet.global.common.code;

import com.baekho.bridgenet.global.common.code.parent.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {
    USERNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 username 입니다."),
    ADDRESS_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 유저입니다."),
    UNKNOWN_USER(HttpStatus.UNAUTHORIZED, "존재 하지 않는 유저입니다."),
    INCORRECT_USERINFO(HttpStatus.UNAUTHORIZED, "유저 정보가 일치 하지 않습니다."),

    REFRESH_TOKEN_ID_IS_NULL(HttpStatus.BAD_REQUEST, "refreshTokenID가 비었습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "refershToken이 만료되었습니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "refershToken을 찾을 수 없습니다."),

    NONCE_EXPIRED_DATE(HttpStatus.UNAUTHORIZED, "만료된 논스값입니다."),
    NONCE_NOT_FOUND(HttpStatus.UNAUTHORIZED, "논스값을 찾을 수 없습니다."),

    INCORRECT_SIGNATURE(HttpStatus.BAD_REQUEST, "잘못된 서명값입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
