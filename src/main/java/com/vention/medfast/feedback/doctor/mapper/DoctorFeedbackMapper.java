package com.vention.medfast.feedback.doctor.mapper;

import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.feedback.doctor.dto.DoctorFeedbackCreateDto;
import com.vention.medfast.feedback.doctor.dto.DoctorFeedbackDto;
import com.vention.medfast.feedback.doctor.model.DoctorFeedback;
import com.vention.medfast.patient.model.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DoctorFeedbackMapper {

	@Mapping(target = "patientId", source = "patient.id")
	@Mapping(target = "doctorId", source = "doctor.id")
	DoctorFeedbackDto toDto(DoctorFeedback entity);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	DoctorFeedback toEntity(DoctorFeedbackCreateDto dto, Doctor doctor, Patient patient);

}
