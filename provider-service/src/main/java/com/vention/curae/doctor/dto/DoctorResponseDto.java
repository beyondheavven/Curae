package com.vention.curae.doctor.dto;

import com.vention.curae.clinic.dto.ClinicBaseDto;
import com.vention.curae.specialization.dto.SpecializationBaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

@Builder
@Schema(description = "Represents returned doctor's details")
public record DoctorResponseDto(Long id, String firstName, String lastName, String bio, String email,
		String licenseNumber, String phoneNumber, List<SpecializationBaseDto> specializations,
		List<ClinicBaseDto> clinics) {
}