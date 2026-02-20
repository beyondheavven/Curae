package com.vention.medfast.password.event;

import com.vention.medfast.email.model.EmailTokenType;
import com.vention.medfast.user.model.User;

public record PasswordResetCreatedEvent(User user, EmailTokenType type, String code, String rawToken) {
}
