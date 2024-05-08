package com.onmydesk.backend.error.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommentErrorCode implements ErrorCode {

    NO_PERMISSION(HttpStatus.UNAUTHORIZED, "User not have permission to comment"),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Comment not found"),
    COMMENT_NOT_BELONGS_TO_POST(HttpStatus.BAD_REQUEST, "Comment not belongs to the post"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
