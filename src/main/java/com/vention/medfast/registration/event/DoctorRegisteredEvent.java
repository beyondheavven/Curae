package com.vention.medfast.registration.event;

public record DoctorRegisteredEvent(Long userId, String tempPassword) {
}
