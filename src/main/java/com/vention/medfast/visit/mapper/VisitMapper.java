package com.vention.medfast.visit.mapper;

import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.service.model.ProvidedService;
import com.vention.medfast.visit.dto.VisitCreateDto;
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
	Visit toEntity(VisitCreateDto dto, Doctor doctor, Patient patient, ProvidedService service);

}
