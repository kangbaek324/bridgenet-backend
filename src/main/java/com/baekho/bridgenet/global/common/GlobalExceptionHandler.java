package com.baekho.bridgenet.global.common;

import com.baekho.bridgenet.global.common.exception.AuthException;
import com.baekho.bridgenet.global.common.exception.WhiteListException;
import com.baekho.bridgenet.global.common.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import com.baekho.springClass.dto.ValidationErrorDTO;

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
    public ResponseEntity<ErrorResponse<String>> handleException(Exception ex) {
        log.error("서버 에러 발생: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse<>("서버에 오류가 발생했습니다."));
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponse<String>> authException(AuthException ex) {
        return ResponseEntity.status(ex.getErrorCode().getHttpStatus())
                .body(new ErrorResponse<>(ex.getErrorCode().getMessage()));
    }

    @ExceptionHandler(WhiteListException.class)
    public ResponseEntity<ErrorResponse<String>> whiteListException(WhiteListException ex) {
        return ResponseEntity.status(ex.getErrorCode().getHttpStatus())
                .body(new ErrorResponse<>(ex.getErrorCode().getMessage()));
    }

    // 404
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse<String>> handleResponseResponseEntity(NoHandlerFoundException e) {
        return ResponseEntity.status(404).body(new ErrorResponse<>(e.getMessage()));
    }
}