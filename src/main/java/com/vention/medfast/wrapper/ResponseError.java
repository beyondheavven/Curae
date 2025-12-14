package com.vention.medfast.wrapper;

import lombok.*;
import lombok.experimental.NonFinal;

@Value
@NonFinal
public class ResponseError {
    ErrorCode errorCode;
    String message;
}
