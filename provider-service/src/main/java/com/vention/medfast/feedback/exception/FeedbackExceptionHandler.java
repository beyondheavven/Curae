package com.vention.medfast.feedback.exception;

import com.vention.medfast.wrapper.ErrorCode;
import com.vention.medfast.wrapper.ResponseWrapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.vention.medfast.feedback")
public class FeedbackExceptionHandler {

	@ExceptionHandler(FeedbackNotAllowedException.class)
	public ResponseEntity<ResponseWrapper<?>> handleFeedbackNotAllowed(FeedbackNotAllowedException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(ResponseWrapper.withError(HttpStatus.BAD_REQUEST, ex.getMessage(), ErrorCode.BAD_REQUEST));
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ResponseWrapper<?>> handleNotFound(EntityNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(ResponseWrapper.withError(HttpStatus.NOT_FOUND, ex.getMessage(), ErrorCode.NOT_FOUND));
	}

}