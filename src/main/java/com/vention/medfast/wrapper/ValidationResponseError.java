package com.vention.medfast.wrapper;

import lombok.*;
import java.util.Map;

@Value
public class ValidationResponseError extends ResponseError{
    Map<String, String> fieldErrors;

    @Builder
    public ValidationResponseError(ErrorCode errorCode, String message, Map<String, String> fieldErrors) {
        super(errorCode, message);
        this.fieldErrors = fieldErrors;
    }
}
