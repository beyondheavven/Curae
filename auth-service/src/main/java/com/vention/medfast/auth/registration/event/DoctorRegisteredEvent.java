package com.vention.medfast.auth.registration.event;

public record DoctorRegisteredEvent(Long userId, String tempPassword) {
}
