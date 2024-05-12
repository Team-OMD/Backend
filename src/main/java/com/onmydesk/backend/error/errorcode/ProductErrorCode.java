package com.onmydesk.backend.error.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements ErrorCode {

    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "Product not found"),
    CRAWLING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Crawling failed"),
    API_REQUEST_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "API request failed"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}