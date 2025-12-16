    package com.vention.medfast.wrapper;

    import java.util.Map;
    public record ValidationResponseError(
        Object errorCode,
        String message,
        Map<String, String> fieldErrors
    ) {}
