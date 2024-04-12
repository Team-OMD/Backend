package com.onmydesk.backend.error.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum HeartErrorCode implements ErrorCode {

    HEART_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "Heart already exists"),
    HEART_NOT_FOUND(HttpStatus.NOT_FOUND, "Heart not found"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
