package com.vention.curae.payment.webhook.dto;

public record WebhookProcessingDto(String eventId, String eventType, String status, String message) {
}
