package com.onmydesk.backend.post.exception;

public class PostUpdateException extends RuntimeException {

    public PostUpdateException(Long postId) {
        super("게시글 업데이트 중 문제가 발생했습니다.(게시글 ID: " + postId + ")");
    }

}

