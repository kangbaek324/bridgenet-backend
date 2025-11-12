package com.baekho.bridgenet.global.common.response;

import lombok.Getter;

@Getter
public class SuccessResponse<T>{
    private final boolean success;
    private final String message;
    private final T data;

    public SuccessResponse(String message, T data) {
        this.success = true;
        this.message = message.isEmpty() ? "요청에 성공했습니다." : message;
        this.data = data;
    }
}