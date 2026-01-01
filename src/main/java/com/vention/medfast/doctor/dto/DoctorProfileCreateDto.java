package com.vention.medfast.doctor.dto;

import lombok.Builder;

import java.util.Set;

@Builder
public record DoctorProfileCreateDto(Set<Long> specializationIds, String licenseNumber, String phoneNumber) {
}
