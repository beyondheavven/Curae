package com.vention.medfast.visit.mapper;

import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.visit.dto.VisitCreateDto;
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

}
