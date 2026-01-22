package com.vention.medfast.api.exception;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.vention.medfast.wrapper.ErrorCode;
import com.vention.medfast.wrapper.FieldValidationError;
import com.vention.medfast.wrapper.ResponseWrapper;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(InvalidRefreshTokenException.class)
	public ResponseEntity<ResponseWrapper<?>> handleInvalidRefreshToken(InvalidRefreshTokenException ex) {
		return buildError(HttpStatus.UNAUTHORIZED, ErrorCode.AUTH_ERROR, ex.getMessage());
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ResponseWrapper<?>> handleAccessDenied(AccessDeniedException ex) {
		return buildError(HttpStatus.FORBIDDEN, ErrorCode.AUTH_ERROR, "Access denied");
	}

	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<ResponseWrapper<?>> handleUserNotFound(UsernameNotFoundException ex) {
		return buildError(HttpStatus.UNAUTHORIZED, ErrorCode.AUTH_ERROR, ex.getMessage());
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ResponseWrapper<?>> handleBadCredentials(BadCredentialsException ex) {
		return buildError(HttpStatus.UNAUTHORIZED, ErrorCode.AUTH_ERROR, "Invalid email or password");
	}

	@ExceptionHandler(EmailAlreadyExistsException.class)
	public ResponseEntity<ResponseWrapper<?>> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
		return buildError(HttpStatus.CONFLICT, ErrorCode.BAD_REQUEST, ex.getMessage());
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ResponseWrapper<?>> handleResourceNotFound(EntityNotFoundException ex) {
		return buildError(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, ex.getMessage());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ResponseWrapper<?>> handleIllegalArgument(IllegalArgumentException ex) {
		return buildError(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST, ex.getMessage());
	}

	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<ResponseWrapper<?>> handleIllegalState(IllegalStateException ex) {
		return buildError(HttpStatus.CONFLICT, ErrorCode.BAD_REQUEST, ex.getMessage());
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, org.springframework.http.HttpStatusCode status,
			org.springframework.web.context.request.WebRequest request) {

		List<FieldValidationError> validationErrors = ex.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(err -> new FieldValidationError(err.getField(), err.getRejectedValue(), err.getDefaultMessage()))
			.toList();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(ResponseWrapper.withValidationError(HttpStatus.BAD_REQUEST, "Validation failed", validationErrors));
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ResponseWrapper<?>> handleConstraintViolation(ConstraintViolationException ex) {

		List<FieldValidationError> validationErrors = ex.getConstraintViolations()
			.stream()
			.map(v -> new FieldValidationError(v.getPropertyPath() != null ? v.getPropertyPath().toString() : null,
					v.getInvalidValue(), v.getMessage()))
			.toList();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(ResponseWrapper.withValidationError(HttpStatus.BAD_REQUEST, "Validation failed", validationErrors));
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, org.springframework.http.HttpStatusCode status,
			org.springframework.web.context.request.WebRequest request) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(ResponseWrapper.withError(HttpStatus.BAD_REQUEST, "Malformed JSON or invalid field type/format",
					ErrorCode.BAD_REQUEST));
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ResponseWrapper<?>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
		return buildError(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST, ex.getName() + ": invalid value");
	}

	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
			HttpHeaders headers, org.springframework.http.HttpStatusCode status,
			org.springframework.web.context.request.WebRequest request) {
		String message = ex.getParameterName() + ": missing required parameter";

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(ResponseWrapper.withError(HttpStatus.BAD_REQUEST, message, ErrorCode.BAD_REQUEST));
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ResponseWrapper<?>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
		String rootMessage = getRootMessage(ex).toLowerCase();

		boolean emailUniqueViolation = rootMessage.contains("email") && (rootMessage.contains("unique")
				|| rootMessage.contains("duplicate") || rootMessage.contains("uk_user_email"));

		if (emailUniqueViolation) {
			return buildError(HttpStatus.CONFLICT, ErrorCode.BAD_REQUEST, "User with this email already exists");
		}

		return buildError(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST, "Request violates database constraints");
	}

	@ExceptionHandler(JwtException.class)
	public ResponseEntity<ResponseWrapper<?>> handleJwtException(JwtException ex) {
		return buildError(HttpStatus.UNAUTHORIZED, ErrorCode.AUTH_ERROR, "Invalid or expired token");
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ResponseWrapper<?>> handleUnexpectedException(Exception ex) {
		log.error("Unhandled exception", ex);
		return buildError(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.SERVER_ERROR, "Internal server error");
	}

	@ExceptionHandler(PasswordResetException.class)
	public ResponseEntity<ResponseWrapper<?>> handlePasswordResetException(PasswordResetException ex) {
		return buildError(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST, ex.getMessage());
	}

	@ExceptionHandler(AppointmentException.class)
	public ResponseEntity<ResponseWrapper<?>> handleAppointmentException(AppointmentException ex) {
		return buildError(HttpStatus.CONFLICT, ErrorCode.BAD_REQUEST, ex.getMessage());
	}

	@ExceptionHandler(StripeException.class)
	public ResponseEntity<ResponseWrapper<?>> handleStripeException(StripeException ex) {
		return buildError(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST, ex.getMessage());
	}

	@ExceptionHandler(SignatureVerificationException.class)
	public ResponseEntity<ResponseWrapper<?>> handleSignatureVerificationException(SignatureVerificationException ex) {
		return buildError(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST, "Invalid Stripe signature");
	}

	private ResponseEntity<ResponseWrapper<?>> buildError(HttpStatus status, ErrorCode code, String message) {
		return ResponseEntity.status(status).body(ResponseWrapper.withError(status, message, code));
	}

	private String getRootMessage(Throwable ex) {
		Throwable root = ex;
		while (root.getCause() != null && root.getCause() != root) {
			root = root.getCause();
		}
		return Objects.toString(root.getMessage(), "");
	}

}
