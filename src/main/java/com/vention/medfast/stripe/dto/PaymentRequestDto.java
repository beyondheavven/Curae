package com.vention.medfast.stripe.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "DTO for creating payment")
public record PaymentRequestDto() {

}
