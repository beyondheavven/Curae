package com.vention.curae.api.response;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ErrorResponseDto {

	private Integer statusCode;

	private String message;

}
