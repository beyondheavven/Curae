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
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseWrapper<?>> handleUnexpectedException(
        Exception ex
    ) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                ResponseWrapper.builder()
                    .error(new ResponseError(
                        ErrorCode.SERVER_ERROR,
                        "Internal server error"
                    ))
                    .build()
            );
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ResponseWrapper<?>> handleInvalidRefreshToken(
        InvalidRefreshTokenException ex
    ) {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(
                ResponseWrapper.builder()
                    .error(new ResponseError(
                        ErrorCode.AUTH_ERROR,
                        ex.getMessage()
                    ))
                    .build()
            );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseWrapper<?>> handleAccessDenied(
        AccessDeniedException ex
    ) {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(
                ResponseWrapper.builder()
                    .error(new ResponseError(
                        ErrorCode.AUTH_ERROR,
                        "Access denied"
                    ))
                    .build()
            );
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ResponseWrapper<?>> handleUserNotFound(
        UsernameNotFoundException ex
    ) {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(
                ResponseWrapper.builder()
                    .error(new ResponseError(
                        ErrorCode.AUTH_ERROR,
                        ex.getMessage()
                    ))
                    .build()
            );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ResponseWrapper<?>> handleBadCredentials(
        BadCredentialsException ex
    ) {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(
                ResponseWrapper.builder()
                    .error(new ResponseError(
                        ErrorCode.AUTH_ERROR,
                        "Invalid email or password"
                    ))
                    .build()
            );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseWrapper<?>> handleValidationExceptions(
        MethodArgumentNotValidException ex
    ) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ResponseWrapper.builder()
                    .error(new ResponseError(
                        ErrorCode.VALIDATION_ERROR,
                        "Validation error"
                    ))
                    .build()
            );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ResponseWrapper<?>> handleResourceNotFound(
        EntityNotFoundException ex
    ) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(
                ResponseWrapper.builder()
                    .error(new ResponseError(
                        ErrorCode.NOT_FOUND,
                        ex.getMessage()
                    ))
                    .build()
            );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseWrapper<?>> handleIllegalArgument(
        IllegalArgumentException ex
    ) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ResponseWrapper.builder()
                    .error(new ResponseError(
                        ErrorCode.BAD_REQUEST,
                        ex.getMessage()
                    ))
                    .build()
            );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ResponseWrapper<?>> handleIllegalState(
        IllegalStateException ex
    ) {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(
                ResponseWrapper.builder()
                    .error(new ResponseError(
                        ErrorCode.BAD_REQUEST,
                        ex.getMessage()
                    ))
                    .build()
            );
    }
}
