package com.baekho.bridgenet.global.common;

import com.baekho.bridgenet.global.common.exception.*;
import com.baekho.bridgenet.global.common.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse<List<ValidationErrorDTO>>> handleMethodArgumentNoValidException(MethodArgumentNotValidException e) {
        List<ObjectError> errors = e.getBindingResult().getAllErrors();

        // 첫 번째 에러 메시지 가져오기 (선택사항)
        String message = errors.get(0).getDefaultMessage();

        List<ValidationErrorDTO> errorResults = new ArrayList<>();

        for (ObjectError error : errors) {
            if (error instanceof FieldError) {
                errorResults.add(
                        ValidationErrorDTO.builder()
                                .message(error.getDefaultMessage())
                                .filed(((FieldError) error).getField())
                                .build()
                );
            }
        }

        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse<>(errorResults));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse<String>> handleException(Exception e) {
        log.error("서버 에러 발생: {}", e.getMessage(),e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse<>("서버에 오류가 발생했습니다."));
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponse<String>> authException(AuthException e) {
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(new ErrorResponse<>(e.getErrorCode().getMessage()));
    }

    @ExceptionHandler(WhiteListException.class)
    public ResponseEntity<ErrorResponse<String>> whiteListException(WhiteListException e) {
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(new ErrorResponse<>(e.getErrorCode().getMessage()));
    }

    @ExceptionHandler(ChainException.class)
    public ResponseEntity<ErrorResponse<String>> chinException(ChainException e) {
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(new ErrorResponse<>(e.getErrorCode().getMessage()));
    }

    @ExceptionHandler(BridgeException.class)
    public ResponseEntity<ErrorResponse<String>> whiteListException(BridgeException e) {
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(new ErrorResponse<>(e.getErrorCode().getMessage()));
    }

    @ExceptionHandler(BlockchainException.class)
    public ResponseEntity<ErrorResponse<String>> blockchainException(BlockchainException e) {
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(new ErrorResponse<>(e.getErrorCode().getMessage()));
    }

    // 400
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse<String>> illegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse<>("잘못된 인자값 입니다."));
    }

    // 401
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse<String>> accessDeniedException(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse<>("접근 권한이 없습니다"));
    }

    // 404
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse<String>> noHandlerFoundException(NoHandlerFoundException e) {
        return ResponseEntity.status(404).body(new ErrorResponse<>(e.getMessage()));
    }

    //405
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse<String>> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity.status(405).body(new ErrorResponse<>(e.getMessage()));
    }
}