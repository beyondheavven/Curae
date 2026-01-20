package com.vention.medfast.wrapper;

import java.time.OffsetDateTime;
import java.util.List;

public record ValidationError(ErrorCode errorCode, String message, List<FieldValidationError> validationErrors,
		OffsetDateTime timestamp) implements ResponseError {

	public ValidationError {
		errorCode = ErrorCode.VALIDATION_ERROR;
	}

	public ValidationError(String message, List<FieldValidationError> validationErrors, OffsetDateTime timestamp) {
		this(ErrorCode.VALIDATION_ERROR, message, validationErrors, timestamp);
	}
}
