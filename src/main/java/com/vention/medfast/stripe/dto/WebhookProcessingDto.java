package com.vention.medfast.stripe.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "DTO for webhook processing")
public record WebhookProcessingDto(String eventId, String eventType, String status, String message) {
}
