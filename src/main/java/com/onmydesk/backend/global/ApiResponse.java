package com.onmydesk.backend.global;

import lombok.*;

import jakarta.annotation.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ApiResponse {

    private static final String STATUS_SUCCESS = "success";
    private static final String STATUS_FAIL = "fail";
    private static final String STATUS_ERROR = "error";

    private  <T, E> ResponseEntity<Object> get(String status, @Nullable String message, @Nullable T data, @Nullable E errors, HttpStatus httpStatus) {

        if (status.equals(STATUS_SUCCESS)) {
            return new ResponseEntity<>(SucceededBody.builder()
                    .status(status)
                    .message(message)
                    .data(data)
                    .build(),
                    httpStatus);
        } else if (status.equals(STATUS_ERROR)) {
            return new ResponseEntity<>(ErroredBody.builder()
                    .status(status)
                    .message(message)
                    .build(),
                    httpStatus);
        } else {
            throw new RuntimeException("Api Response Error");
        }
    }


//     성공 응답 반환 (상태, 메시지, 데이터)
//     {
//          "status" : "success",
//          "message" : "success message",
//          "data" : "배열 또는 단일 데이터"
//     }
    public <T> ResponseEntity<Object> success(String message, T data, HttpStatus httpStatus) {
        return get(STATUS_SUCCESS, message, data, null, httpStatus);
    }


//     성공 응답 반환 (상태, 데이터)
//     {
//          "status" : "success",
//          "message" : null,
//          "data" : "배열 또는 단일 데이터"
//     }
    public <T> ResponseEntity<Object> success(T data, HttpStatus httpStatus) {
        return get(STATUS_SUCCESS, null, data, null, httpStatus);
    }

//     성공 응답 반환 (메시지, 데이터)
//     {
//          "status" : "success",
//          "message" : "success message",
//          "data" : null
//     }

    public <T> ResponseEntity<Object> success(String message, HttpStatus httpStatus) {
        return get(STATUS_SUCCESS, message, null, null, httpStatus);
    }

//     성공 응답 반환 (상태)
//     {
//          "status" : "success",
//          "message" : null,
//          "data" : null
//     }
    public ResponseEntity<Object> success(HttpStatus httpStatus) {
        return get(STATUS_SUCCESS, null, null, null, httpStatus);
    }

//     예외 발생 시 에러 응답 반환
//     {
//          "status" : "error",
//          "message" : "custom error message"
//     }
    public ResponseEntity<Object> error(String message, HttpStatus httpStatus) {
        return get(STATUS_ERROR, message, null, null, httpStatus);
    }

    // 성공 응답 객체 바디
    @Builder
    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SucceededBody<T> {

        private String status;
        private String message;
        private T data;
    }

    // 오류 응답 객체 바디
    @Builder
    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ErroredBody {

        private String status;
        private String message;
    }

}
