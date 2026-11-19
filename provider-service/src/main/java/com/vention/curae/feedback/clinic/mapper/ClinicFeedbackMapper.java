package com.vention.curae.feedback.clinic.mapper;

import com.vention.curae.clinic.model.Clinic;
import com.vention.curae.feedback.clinic.dto.ClinicFeedbackCreateDto;
import com.vention.curae.feedback.clinic.dto.ClinicFeedbackDto;
import com.vention.curae.feedback.clinic.model.ClinicFeedback;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClinicFeedbackMapper {

	@Mapping(target = "patientId", source = "patientId")
	@Mapping(target = "clinicId", source = "clinic.id")
	ClinicFeedbackDto toDto(ClinicFeedback entity);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "clinic", source = "clinic")
	@Mapping(target = "patientId", source = "patientId")
	@Mapping(target = "rating", source = "dto.rating")
	@Mapping(target = "summary", source = "dto.summary")
	ClinicFeedback toEntity(ClinicFeedbackCreateDto dto, Clinic clinic, Long patientId);

}