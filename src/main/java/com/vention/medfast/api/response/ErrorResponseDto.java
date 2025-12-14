package com.vention.medfast.api.response;

import lombok.*;

import java.net.http.HttpResponse;

@Getter
@AllArgsConstructor
@Builder
public class ErrorResponseDto {

	private  Integer statusCode;
	private  String message;

}
