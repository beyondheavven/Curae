package com.vention.medfast.api.exception;

import com.vention.medfast.api.response.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

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

}
