package com.vention.medfast.wrapper;

import java.time.OffsetDateTime;

public sealed interface ResponseError permits ValidationError, CommonError {

	ErrorCode errorCode();

	String message();

	OffsetDateTime timestamp();

}
