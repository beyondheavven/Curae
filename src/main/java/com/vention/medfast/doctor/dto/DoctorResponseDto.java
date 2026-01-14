package com.vention.medfast.doctor.dto;

import com.vention.medfast.clinic.dto.ClinicBaseDto;
import com.vention.medfast.specialization.dto.SpecializationBaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "Represents returned doctor's details")
public record DoctorResponseDto(Long id, String firstName, String lastName, String bio, String email,
		String licenseNumber, String phoneNumber, List<SpecializationBaseDto> specializations,
		List<ClinicBaseDto> clinics) {
}
