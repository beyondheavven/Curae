package com.vention.curae.wrapper;

import java.time.OffsetDateTime;

public record CommonError(ErrorCode errorCode, String message, OffsetDateTime timestamp) implements ResponseError {
}
