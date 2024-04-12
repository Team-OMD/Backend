package com.onmydesk.backend.error.handler;

import com.onmydesk.backend.error.errorcode.CommonErrorCode;
import com.onmydesk.backend.error.errorcode.ErrorCode;
import com.onmydesk.backend.error.exception.RestApiException;
import com.onmydesk.backend.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ApiResponse apiResponse;

    @ExceptionHandler(RestApiException.class)
    public ResponseEntity<?> handleCustomException(RestApiException e) {
        ErrorCode errorCode = e.getErrorCode();
        return apiResponse.error(errorCode.getMessage(), errorCode.getHttpStatus());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException e) {
        ErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;
        return apiResponse.error(errorCode.getMessage(), errorCode.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllException(Exception e) {
        ErrorCode errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;
        return apiResponse.error(e.getMessage(), errorCode.getHttpStatus());
    }
}
