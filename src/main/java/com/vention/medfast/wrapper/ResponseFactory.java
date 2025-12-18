package com.vention.medfast.wrapper;

import org.springframework.http.HttpStatus;

public class ResponseFactory {

    public static <T> ResponseWrapper<T> ok(T data) {
        return ResponseWrapper.<T>builder()
            .data(data)
            .status(HttpStatus.OK)
            .statusCode(HttpStatus.OK.value())
            .build();
    }

    public static <T> ResponseWrapper<T> badRequest(String message, ErrorCode code) {
        return ResponseWrapper.<T>builder()
            .error(new ResponseError(code, message))
            .status(HttpStatus.BAD_REQUEST)
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .build();
    }

    public static <T> ResponseWrapper<T> withStatus(HttpStatus status, T data) {
        return ResponseWrapper.<T>builder()
            .data(data)
            .status(status)
            .statusCode(status.value())
            .build();
    }
}
