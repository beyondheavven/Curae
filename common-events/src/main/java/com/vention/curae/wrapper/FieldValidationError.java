package com.vention.curae.wrapper;

public record FieldValidationError(String field, Object rejectedValue, String message) {
}
