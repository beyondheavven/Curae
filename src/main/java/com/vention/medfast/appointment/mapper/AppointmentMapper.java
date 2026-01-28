package com.vention.medfast.appointment.mapper;

import com.vention.medfast.appointment.dto.AppointmentCreateDto;
import com.vention.medfast.appointment.dto.AppointmentCreateWithLabsDto;
import com.vention.medfast.appointment.dto.AppointmentUpdateDto;
import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.referral.model.Referral;
import com.vention.medfast.patient.model.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "date", source = "dto.date")
	@Mapping(target = "time", source = "dto.time")
	@Mapping(target = "patient", source = "patient")
	@Mapping(target = "referral", source = "referral")
	@Mapping(target = "status", ignore = true)
	@Mapping(target = "type", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "notifications", ignore = true)
	Appointment toAppointment(AppointmentCreateDto dto, Patient patient, Referral referral);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "date", source = "dto.date")
	@Mapping(target = "time", source = "dto.time")
	@Mapping(target = "patient", source = "patient")
	@Mapping(target = "referral", ignore = true)
	@Mapping(target = "status", ignore = true)
	@Mapping(target = "type", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "notifications", ignore = true)
	Appointment toLabAppointment(AppointmentCreateWithLabsDto dto, Patient patient);

	@Mapping(target = "id", ignore = true)
	void updateFromDto(AppointmentUpdateDto dto, @MappingTarget Appointment appointment);

}
