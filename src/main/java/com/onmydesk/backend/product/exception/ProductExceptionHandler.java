package com.onmydesk.backend.product.exception;


import com.onmydesk.backend.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RequiredArgsConstructor
@RestControllerAdvice
public class ProductExceptionHandler {

    private final ApiResponse apiResponse;

    // 상품을 찾을 수 없는 경우의 예외 처리
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<?> handleProductNotFoundException(ProductNotFoundException e) {
        return apiResponse.fail(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        return apiResponse.error("서버 내부 오류", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
