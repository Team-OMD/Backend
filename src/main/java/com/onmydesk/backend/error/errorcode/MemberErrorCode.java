package com.onmydesk.backend.error.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {

    DUPLICATE_MEMBER(HttpStatus.BAD_REQUEST, "Member already exists"),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "Member not found"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}