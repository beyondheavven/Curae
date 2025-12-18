package com.vention.medfast.wrapper;

public record ResponseError(
    ErrorCode errorCode,
    String message
) {}

