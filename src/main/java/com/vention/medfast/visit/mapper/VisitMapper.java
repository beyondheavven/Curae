package com.vention.medfast.visit.mapper;

import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.specialization.model.Specialization;
import com.vention.medfast.visit.dto.DoctorForVisitResponseDto;
import com.vention.medfast.visit.dto.VisitCreateDto;
import com.vention.medfast.visit.dto.VisitResponseDto;
import com.vention.medfast.visit.model.Visit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VisitMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "appointment", source = "appointment")
	@Mapping(target = "diagnosis", source = "dto.diagnosis")
	@Mapping(target = "symptoms", source = "dto.symptoms")
	@Mapping(target = "medicalReport", source = "dto.medicalReport")
	Visit toEntity(VisitCreateDto dto, Appointment appointment);

	@Mapping(source = "appointment.doctor", target = "doctor")
	@Mapping(source = "clinic.address", target = "address")
	@Mapping(source = "clinic.name", target = "clinic")
	@Mapping(source = "appointment.date", target = "date")
	@Mapping(source = "appointment.time", target = "time")
	@Mapping(source = "appointment.status", target = "status")
	VisitResponseDto toVisitResponseDto(Visit visit);

	@Mapping(source = "user.firstName", target = "firstName")
	@Mapping(source = "user.lastName", target = "lastName")
	@Mapping(source = "specializations", target = "specializations")
	DoctorForVisitResponseDto toDoctorForVisitDto(com.vention.medfast.doctor.model.Doctor doctor);

	default String map(Specialization specialization) {
		return specialization.getName();
	}

}
