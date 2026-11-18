package com.vention.medfast.registration.event;

import com.vention.medfast.registration.dto.DoctorRegistrationRequestDto;

public record DoctorRegisteredEvent(Long userId, String tempPassword, DoctorRegistrationRequestDto request) {
}
