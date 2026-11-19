package com.vention.medfast.feedback.doctor.mapper;

import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.feedback.doctor.dto.DoctorFeedbackCreateDto;
import com.vention.medfast.feedback.doctor.dto.DoctorFeedbackDto;
import com.vention.medfast.feedback.doctor.model.DoctorFeedback;
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