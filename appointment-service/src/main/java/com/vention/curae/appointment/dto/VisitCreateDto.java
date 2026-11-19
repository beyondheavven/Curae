package com.vention.curae.appointment.dto;

import jakarta.validation.constraints.NotNull;

public record VisitCreateDto(String diagnosis, String symptoms, String medicalReport, @NotNull Long doctorId,
		@NotNull Long serviceId, @NotNull Long clinicId, String doctorFirstName, String doctorLastName,
		String clinicName, String city) {
}
