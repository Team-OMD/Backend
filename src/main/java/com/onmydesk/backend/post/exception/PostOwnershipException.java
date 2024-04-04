package com.onmydesk.backend.post.exception;

public class PostOwnershipException extends IllegalArgumentException {

    public PostOwnershipException(Long postId) {
        super("게시글 수정, 삭제 권한이 없습니다.(게시글 ID: " + postId + ")");
    }
}
