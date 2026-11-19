package com.vention.curae.wrapper;

import java.time.OffsetDateTime;

public sealed interface ResponseError permits ValidationError, CommonError {

	ErrorCode errorCode();

	String message();

	OffsetDateTime timestamp();

}
