package com.vention.medfast.wrapper;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import org.springframework.http.HttpStatus;

@Value
@Builder
@JsonDeserialize(builder = ResponseWrapper.ResponseWrapperBuilder.class)
public class ResponseWrapper<T> {
    T data;
    ResponseError error;
    HttpStatus status;
    int statusCode;
}
