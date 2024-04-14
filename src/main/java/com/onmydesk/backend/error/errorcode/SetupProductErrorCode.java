package com.onmydesk.backend.error.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SetupProductErrorCode implements ErrorCode {

    SETUP_PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "SetupProduct not found"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
