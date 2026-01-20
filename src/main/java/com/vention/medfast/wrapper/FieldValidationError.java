package com.vention.medfast.wrapper;

public record FieldValidationError(String field, Object rejectedValue, String message) {
}
