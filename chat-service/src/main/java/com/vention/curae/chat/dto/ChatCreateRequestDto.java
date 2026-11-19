package com.vention.curae.chat.dto;

import jakarta.validation.constraints.NotNull;

public record ChatCreateRequestDto(@NotNull Long doctorId) {
}
