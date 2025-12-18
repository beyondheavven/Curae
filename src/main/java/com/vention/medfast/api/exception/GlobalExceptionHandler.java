package com.vention.medfast.api.exception;

import com.vention.medfast.wrapper.ErrorCode;
import com.vention.medfast.wrapper.ResponseError;
import com.vention.medfast.wrapper.ResponseWrapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseWrapper<?>> handleUnexpectedException(
        Exception ex
    ) {
        return buildError(
            HttpStatus.INTERNAL_SERVER_ERROR,
            ErrorCode.SERVER_ERROR,
            "Internal server error"
        );
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ResponseWrapper<?>> handleInvalidRefreshToken(
        InvalidRefreshTokenException ex
    ) {
        return buildError(
            HttpStatus.UNAUTHORIZED,
            ErrorCode.AUTH_ERROR,
            ex.getMessage()
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseWrapper<?>> handleAccessDenied(
        AccessDeniedException ex
    ) {
        return buildError(
            HttpStatus.FORBIDDEN,
            ErrorCode.AUTH_ERROR,
            "Access denied"
        );
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ResponseWrapper<?>> handleUserNotFound(
        UsernameNotFoundException ex
    ) {
        return buildError(
            HttpStatus.UNAUTHORIZED,
            ErrorCode.AUTH_ERROR,
            ex.getMessage()
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ResponseWrapper<?>> handleBadCredentials(
        BadCredentialsException ex
    ) {
        return buildError(
            HttpStatus.UNAUTHORIZED,
            ErrorCode.AUTH_ERROR,
            "Invalid email or password"
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseWrapper<?>> handleValidationExceptions(
        MethodArgumentNotValidException ex
    ) {
        return buildError(
            HttpStatus.BAD_REQUEST,
            ErrorCode.VALIDATION_ERROR,
            "Validation error"
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ResponseWrapper<?>> handleResourceNotFound(
        EntityNotFoundException ex
    ) {
        return buildError(
            HttpStatus.NOT_FOUND,
            ErrorCode.NOT_FOUND,
            ex.getMessage()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseWrapper<?>> handleIllegalArgument(
        IllegalArgumentException ex
    ) {
        return buildError(
            HttpStatus.BAD_REQUEST,
            ErrorCode.BAD_REQUEST,
            ex.getMessage()
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ResponseWrapper<?>> handleIllegalState(
        IllegalStateException ex
    ) {
        return buildError(
            HttpStatus.CONFLICT,
            ErrorCode.BAD_REQUEST,
            ex.getMessage()
        );
    }

    private ResponseEntity<ResponseWrapper<?>> buildError(
        HttpStatus status,
        ErrorCode code,
        String message
    ) {
        return ResponseEntity
            .status(status)
            .body(
                ResponseWrapper.builder()
                    .error(new ResponseError(code, message))
                    .build()
            );
    }
}
