package com.vention.medfast.chat.message.dto;

import java.time.Instant;

public record MessageResponseDto(Long id, Long chatId, Long senderId, String content, Instant createdAt) {
}
