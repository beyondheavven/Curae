package com.vention.curae.chat.websocket.dto;

import jakarta.validation.constraints.NotBlank;

public record ChatMessageSendWsDto(@NotBlank String content) {
}
