package com.vention.medfast.event;

import java.io.Serializable;
import java.time.Instant;

public record UserRegisteredEvent(
        Long userId,
        String email,
        String firstName,
        String lastName,
        String verificationToken,
        Instant occurredAt
) implements Serializable {
    public UserRegisteredEvent {
        if (occurredAt == null) {
            occurredAt = Instant.now();
        }
    }
}
