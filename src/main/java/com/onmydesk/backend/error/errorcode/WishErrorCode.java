package com.onmydesk.backend.error.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum WishErrorCode implements ErrorCode {

    WISH_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "Wish already exists"),
    WISH_NOT_FOUND(HttpStatus.NOT_FOUND, "Wish not found"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
