package com.onmydesk.backend.heart.exception;

public class HeartNotFoundException extends RuntimeException {
    public HeartNotFoundException(Long postId) {
        super("취소할 좋아요가 없습니다.(게시글 ID: " + postId +")");
    }
}
