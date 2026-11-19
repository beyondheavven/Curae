package com.vention.curae.chat.exception;

import com.vention.curae.api.exception.ChatOperationException;
import com.vention.curae.wrapper.ErrorCode;
import com.vention.curae.wrapper.ResponseWrapper;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.vention.curae.chat")
public class ChatExceptionHandler {

	@ExceptionHandler(ChatOperationException.class)
	public ResponseEntity<ResponseWrapper<?>> handleChatOperation(ChatOperationException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(ResponseWrapper.withError(HttpStatus.BAD_REQUEST, ex.getMessage(), ErrorCode.BAD_REQUEST));
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ResponseWrapper<?>> handleNotFound(EntityNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(ResponseWrapper.withError(HttpStatus.NOT_FOUND, ex.getMessage(), ErrorCode.NOT_FOUND));
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ResponseWrapper<?>> handleAccessDenied(AccessDeniedException ex) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
			.body(ResponseWrapper.withError(HttpStatus.FORBIDDEN, ex.getMessage(), ErrorCode.AUTH_ERROR));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ResponseWrapper<?>> handleValidation(MethodArgumentNotValidException ex) {
		List<com.vention.curae.wrapper.FieldValidationError> errors = ex.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(error -> new com.vention.curae.wrapper.FieldValidationError(error.getField(), error.getRejectedValue(),
					error.getDefaultMessage()))
			.toList();
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(ResponseWrapper.withValidationError(HttpStatus.BAD_REQUEST, "Validation failed", errors));
	}

}