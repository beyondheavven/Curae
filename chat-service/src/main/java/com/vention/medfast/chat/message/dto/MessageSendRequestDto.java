package com.vention.medfast.chat.message.dto;

import jakarta.validation.constraints.NotBlank;

public record MessageSendRequestDto(@NotBlank String content) {
}
