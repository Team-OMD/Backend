package com.onmydesk.backend.error.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PostErrorCode implements ErrorCode {

    NO_PERMISSION(HttpStatus.UNAUTHORIZED, "User not have permission to post"),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "Post not found"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
