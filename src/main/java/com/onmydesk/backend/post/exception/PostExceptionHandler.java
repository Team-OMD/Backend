package com.onmydesk.backend.post.exception;

import com.onmydesk.backend.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RequiredArgsConstructor
@RestControllerAdvice
public class PostExceptionHandler {

    private final ApiResponse apiResponse;

    // 게시글을 찾을 수 없는 경우 예외 처리
    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<?> handlePostNotFoundException(PostNotFoundException e) {
        return apiResponse.error(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    // 게시글 수정, 삭제 권한이 없는 경우 예외처리
    @ExceptionHandler(PostOwnershipException.class)
    public ResponseEntity<?> handlePostOwnershipException(PostOwnershipException e) {
        return apiResponse.error(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    // 게시글 업데이트 중 발생한 예외 처리
    @ExceptionHandler(PostUpdateException.class)
    public ResponseEntity<?> handlePostUpdateException(PostUpdateException e) {
        return apiResponse.error(e.getMessage(), HttpStatus.NOT_FOUND);
    }

}
