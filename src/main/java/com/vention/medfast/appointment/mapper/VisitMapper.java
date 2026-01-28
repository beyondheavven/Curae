package com.vention.medfast.appointment.mapper;

import com.vention.medfast.specialization.model.Specialization;
import com.vention.medfast.appointment.dto.DoctorForVisitResponseDto;
import com.vention.medfast.appointment.dto.VisitCreateDto;
import com.vention.medfast.appointment.dto.VisitResponseDto;
import com.vention.medfast.appointment.model.Visit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VisitMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "diagnosis", source = "dto.diagnosis")
	@Mapping(target = "symptoms", source = "dto.symptoms")
	@Mapping(target = "medicalReport", source = "dto.medicalReport")

	@Mapping(target = "doctor", ignore = true)
	@Mapping(target = "clinic", ignore = true)
	@Mapping(target = "providedService", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "notifications", ignore = true)
	Visit toEntity(VisitCreateDto dto);

	@Mapping(source = "doctor", target = "doctor")
	@Mapping(source = "clinic.address", target = "address")
	@Mapping(source = "clinic.name", target = "clinic")
	@Mapping(source = "date", target = "date")
	@Mapping(source = "time", target = "time")
	@Mapping(source = "status", target = "status")
	VisitResponseDto toVisitResponseDto(Visit visit);

	@Mapping(source = "user.firstName", target = "firstName")
	@Mapping(source = "user.lastName", target = "lastName")
	@Mapping(source = "specializations", target = "specializations")
	DoctorForVisitResponseDto toDoctorForVisitDto(com.vention.medfast.doctor.model.Doctor doctor);

	default String map(Specialization specialization) {
		return specialization.getName();
	}

}
