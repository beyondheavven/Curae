package com.vention.medfast.labresult.mapper;

import com.vention.medfast.appointment.dto.LabResultDTO;
import com.vention.medfast.appointment.dto.PatientLabHistoryDTO;
import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.labresult.model.LabResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LabResultMapper {

	@Mapping(target = "appointmentId", source = "id")
	@Mapping(target = "date", source = "date")
	@Mapping(target = "appointmentStatus", source = "status")
	@Mapping(target = "tests", source = "labResults")
	PatientLabHistoryDTO toHistoryDto(Appointment appointment);

	@Mapping(target = "testName", source = "labTest.name")
	LabResultDTO toLabResultDto(LabResult labResult);

}
