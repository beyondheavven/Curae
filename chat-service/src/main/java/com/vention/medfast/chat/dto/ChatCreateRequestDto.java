package com.vention.medfast.chat.dto;

import jakarta.validation.constraints.NotNull;

public record ChatCreateRequestDto(@NotNull Long doctorId) {
}
