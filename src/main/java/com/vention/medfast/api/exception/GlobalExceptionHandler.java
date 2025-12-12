package com.vention.medfast.api.exception;

import com.vention.medfast.api.response.ErrorResponseDto;
import com.vention.medfast.api.response.ValidationErrorResponseDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponseDto> handleUnexpectedException(Exception ex) {
		ErrorResponseDto error = ErrorResponseDto.builder()
			.statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
			.message("Internal server error")
			.build();

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}

	@ExceptionHandler(InvalidRefreshTokenException.class)
	public ResponseEntity<ErrorResponseDto> handleInvalidRefreshToken(InvalidRefreshTokenException ex) {
		ErrorResponseDto error = ErrorResponseDto.builder()
			.statusCode(HttpStatus.UNAUTHORIZED.value())
			.message(ex.getMessage())
			.build();

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponseDto> handleAccessDenied(AccessDeniedException ex) {
		ErrorResponseDto error = ErrorResponseDto.builder()
			.statusCode(HttpStatus.FORBIDDEN.value())
			.message("Access denied")
			.build();

		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
	}

	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<ErrorResponseDto> handleUserNotFound(UsernameNotFoundException ex) {
		ErrorResponseDto error = ErrorResponseDto.builder()
			.statusCode(HttpStatus.UNAUTHORIZED.value())
			.message(ex.getMessage())
			.build();

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ErrorResponseDto> handleBadCredentials(BadCredentialsException ex) {
		ErrorResponseDto error = ErrorResponseDto.builder()
			.statusCode(HttpStatus.UNAUTHORIZED.value())
			.message("Invalid email or password")
			.build();

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ValidationErrorResponseDto> handleValidationExceptions(MethodArgumentNotValidException ex) {

		Map<String, String> errors = new HashMap<>();

		ex.getBindingResult()
			.getFieldErrors()
			.forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

		return new ResponseEntity<>(ValidationErrorResponseDto.builder()
			.message("Validation error")
			.statusCode(400)
			.fieldErrors(errors)
			.build(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ErrorResponseDto> handleResourceNotFound(EntityNotFoundException ex) {
		ErrorResponseDto error = ErrorResponseDto.builder().message(ex.getMessage()).statusCode(404).build();

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}

}
