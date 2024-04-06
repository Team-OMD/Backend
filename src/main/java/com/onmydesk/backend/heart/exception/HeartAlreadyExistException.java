package com.onmydesk.backend.heart.exception;

public class HeartAlreadyExistException extends Exception {
    public HeartAlreadyExistException(Long postId) {
        super("이미 좋아요를 눌렀습니다.(게시글 ID: " + postId +")");
    }
}
