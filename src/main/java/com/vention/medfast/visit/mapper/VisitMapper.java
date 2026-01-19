package com.vention.medfast.visit.mapper;

import com.vention.medfast.clinic.model.Clinic;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.service.model.ProvidedService;
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
	@Mapping(target = "appointment.date", source = "dto.scheduledDate")
	@Mapping(target = "appointment.time", source = "dto.scheduledTime")
	@Mapping(target = "appointment.doctor", source = "doctor")
	@Mapping(target = "appointment.patient", source = "patient")
	@Mapping(target = "appointment.service", source = "service")
	@Mapping(target = "appointment.status", constant = "SCHEDULED")
	Visit toEntity(VisitCreateDto dto, Doctor doctor, Patient patient, ProvidedService service, Clinic clinic);

	@Mapping(source = "appointment.doctor", target = "doctor")
	@Mapping(source = "clinic.address", target = "address")
	@Mapping(source = "clinic.name", target = "clinic")
	@Mapping(source = "appointment.date", target = "date")
	@Mapping(source = "appointment.time", target = "time")
	@Mapping(source = "appointment.status", target = "status")
	VisitResponseDto toVisitResponseDto(Visit visit);

	@Mapping(source = "user.firstName", target = "firstName")
	@Mapping(source = "user.lastName", target = "lastName")
	DoctorForVisitResponseDto toDoctorForVisitDto(Doctor doctor);

	default String map(Specialization specialization) {
		return specialization.getName();
	}

}
