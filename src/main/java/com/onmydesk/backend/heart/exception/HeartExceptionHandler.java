package com.onmydesk.backend.heart.exception;

import com.onmydesk.backend.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RequiredArgsConstructor
@RestControllerAdvice
public class HeartExceptionHandler {

    private final ApiResponse apiResponse;

    // 좋아요를 이미 누른 경우 예외처리
    @ExceptionHandler(HeartAlreadyExistException.class)
    public ResponseEntity<?> handleHeartAlreadyExistException(HeartAlreadyExistException e) {
        return apiResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // 좋아요를 이미 취소한 경우 예외처리
    @ExceptionHandler(HeartNotFoundException.class)
    public ResponseEntity<?> handlePostOwnershipException(HeartNotFoundException e) {
        return apiResponse.error(e.getMessage(), HttpStatus.NOT_FOUND);
    }

}