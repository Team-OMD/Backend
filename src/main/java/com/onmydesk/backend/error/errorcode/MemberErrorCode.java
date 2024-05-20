package com.onmydesk.backend.error.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {

    DUPLICATE_MEMBER(HttpStatus.BAD_REQUEST, "Member already exists"),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "Member not found"),
    DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "This nickname is already using"),
    INVALID_ACCESS_TOKEN(HttpStatus.BAD_REQUEST,"Invalid Access Token"),
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST,"Invalid Refresh Token")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}