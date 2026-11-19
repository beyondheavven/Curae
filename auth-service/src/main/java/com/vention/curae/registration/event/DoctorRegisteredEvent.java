package com.vention.curae.registration.event;

import com.vention.curae.registration.dto.DoctorRegistrationRequestDto;

public record DoctorRegisteredEvent(Long userId, String tempPassword, DoctorRegistrationRequestDto request) {
}
