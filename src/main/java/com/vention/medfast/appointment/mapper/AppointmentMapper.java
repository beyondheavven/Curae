package com.vention.medfast.appointment.mapper;

import com.vention.medfast.appointment.dto.AppointmentCreateWithLabsDto;
import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.patient.model.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

	@Mapping(target = "patient", source = "patient")
	@Mapping(target = "date", source = "dto.date")
	@Mapping(target = "time", source = "dto.time")
	@Mapping(target = "status", constant = "SCHEDULED")
	Appointment toEntity(AppointmentCreateWithLabsDto dto, Patient patient);

}
