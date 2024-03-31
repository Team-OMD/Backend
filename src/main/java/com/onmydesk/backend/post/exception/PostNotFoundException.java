package com.onmydesk.backend.post.exception;

public class PostNotFoundException extends IllegalArgumentException {

    public PostNotFoundException(Long postId) {
        super("해당 게시글이 없습니다.(게시글 ID: " + postId +")");
    }

}
