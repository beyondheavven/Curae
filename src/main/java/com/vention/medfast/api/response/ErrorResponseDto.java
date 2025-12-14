package com.vention.medfast.api.response;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ErrorResponseDto {

    private Integer statusCode;
    private String message;
}
