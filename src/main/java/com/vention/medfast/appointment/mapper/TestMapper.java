package com.vention.medfast.appointment.mapper;

import com.vention.medfast.appointment.dto.TestCreateWithLabsDto;
import com.vention.medfast.appointment.model.Test;
import com.vention.medfast.clinic.model.Clinic;
import com.vention.medfast.patient.model.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TestMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "date", source = "dto.date")
	@Mapping(target = "time", source = "dto.time")
	@Mapping(target = "patient", source = "patient")
	@Mapping(target = "clinic", source = "clinic")
	@Mapping(target = "referral", ignore = true)
	@Mapping(target = "status", ignore = true)
	@Mapping(target = "type", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "notifications", ignore = true)
	Test toLabAppointment(TestCreateWithLabsDto dto, Patient patient, Clinic clinic);

}
