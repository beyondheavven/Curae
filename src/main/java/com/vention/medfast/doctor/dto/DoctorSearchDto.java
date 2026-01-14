package com.vention.medfast.doctor.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

@Schema(description = "DTO for doctor search by firstName, lastName and licenseNumber, filtered by (specializations, clinics)")
public record DoctorSearchDto(String query, Set<Long> specializationIds, Set<Long> clinicIds) {
}
