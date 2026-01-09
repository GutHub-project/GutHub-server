package com.guthub.guthubserver.global.exception;

import com.guthub.guthubserver.global.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        // 유효성 검사 실패는 INVALID_PARAMETER로 통일
        return ResponseEntity
                .status(ErrorCode.INVALID_PARAMETER.getHttpStatus())
                .body(ApiResponse.of(ErrorCode.INVALID_PARAMETER.getCode(), errorMessage));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity
                .status(ErrorCode.INVALID_PARAMETER.getHttpStatus())
                .body(ApiResponse.of(ErrorCode.INVALID_PARAMETER.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity
                .status(ErrorCode.FORBIDDEN.getHttpStatus())
                .body(ApiResponse.of(ErrorCode.FORBIDDEN.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        return ResponseEntity
                .status(ErrorCode.USER_NOT_FOUND.getHttpStatus())
                .body(ApiResponse.of(ErrorCode.USER_NOT_FOUND.getCode(), ex.getMessage()));
    }

    // 그 외 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex) {
        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(ApiResponse.of(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), ex.getMessage()));
    }
}
