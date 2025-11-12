package com.baekho.bridgenet.global.common.code.parent;

import org.springframework.http.HttpStatus;

public interface SuccessCode {

    HttpStatus getHttpStatus();
    String getMessage();

}
