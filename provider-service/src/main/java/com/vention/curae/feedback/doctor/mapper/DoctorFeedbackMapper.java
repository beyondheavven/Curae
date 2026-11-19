package com.vention.curae.feedback.doctor.mapper;

import com.vention.curae.doctor.model.Doctor;
import com.vention.curae.feedback.doctor.dto.DoctorFeedbackCreateDto;
import com.vention.curae.feedback.doctor.dto.DoctorFeedbackDto;
import com.vention.curae.feedback.doctor.model.DoctorFeedback;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DoctorFeedbackMapper {

	@Mapping(target = "patientId", source = "patientId")
	@Mapping(target = "doctorId", source = "doctor.id")
	DoctorFeedbackDto toDto(DoctorFeedback entity);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "doctor", source = "doctor")
	@Mapping(target = "patientId", source = "patientId")
	@Mapping(target = "rating", source = "dto.rating")
	@Mapping(target = "summary", source = "dto.summary")
	DoctorFeedback toEntity(DoctorFeedbackCreateDto dto, Doctor doctor, Long patientId);

}