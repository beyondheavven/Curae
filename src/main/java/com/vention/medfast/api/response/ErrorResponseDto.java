package com.vention.medfast.api.response;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ErrorResponseDto {

	private final Integer statusCode;

	private final String message;

}
