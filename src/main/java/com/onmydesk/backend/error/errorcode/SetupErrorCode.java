package com.onmydesk.backend.error.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SetupErrorCode implements ErrorCode {

    NO_PERMISSION(HttpStatus.UNAUTHORIZED, "User not have permission to setup"),
    SETUP_NOT_FOUND(HttpStatus.NOT_FOUND, "Setup not found"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
