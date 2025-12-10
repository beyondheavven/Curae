package com.vention.medfast.api.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponseDto {

	private final Integer statusCode;

	private final String message;

}
