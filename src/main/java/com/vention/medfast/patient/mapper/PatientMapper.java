package com.vention.medfast.patient.mapper;

import com.vention.medfast.patient.dto.PatientCreateDto;
import com.vention.medfast.patient.dto.PatientResponseDto;
import com.vention.medfast.patient.model.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PatientMapper{

    Patient toEntity(PatientCreateDto dto);

    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.profileUrl", target = "profileUrl")
    PatientResponseDto toDto(Patient entity);

}
