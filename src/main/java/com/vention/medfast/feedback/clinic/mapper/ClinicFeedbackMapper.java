package com.vention.medfast.feedback.clinic.mapper;

import com.vention.medfast.clinic.model.Clinic;
import com.vention.medfast.feedback.clinic.dto.ClinicFeedbackCreateDto;
import com.vention.medfast.feedback.clinic.dto.ClinicFeedbackDto;
import com.vention.medfast.feedback.clinic.model.ClinicFeedback;
import com.vention.medfast.patient.model.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClinicFeedbackMapper {

	@Mapping(target = "patientId", source = "patient.id")
	@Mapping(target = "clinicId", source = "clinic.id")
	ClinicFeedbackDto toDto(ClinicFeedback entity);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	ClinicFeedback toEntity(ClinicFeedbackCreateDto dto, Clinic clinic, Patient patient);

}
