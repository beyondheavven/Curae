package com.vention.medfast.mail.token;

import com.vention.medfast.mail.model.EmailTokenType;
import com.vention.medfast.user.model.User;

public record EmailTokenCreatedEvent(User user, EmailTokenType type, Integer code, String rawToken) {
}
