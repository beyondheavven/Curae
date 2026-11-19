package com.vention.curae.password.event;

import com.vention.curae.email.model.EmailTokenType;
import com.vention.curae.user.model.User;

public record PasswordResetCreatedEvent(User user, EmailTokenType type, String code, String rawToken) {
}
