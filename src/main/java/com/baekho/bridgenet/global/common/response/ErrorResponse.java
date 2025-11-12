package com.baekho.bridgenet.global.common.response;

import lombok.Getter;

@Getter
public class ErrorResponse<T> {
    private final boolean success;
    private final T message;

    public ErrorResponse(T message) {
        this.success = false;
        this.message = message;
    }
}
