package com.vention.medfast.profile.event;

public record EmailChangedEvent(Long userId, String newEmail) {
}
