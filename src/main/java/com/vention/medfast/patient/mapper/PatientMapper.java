package com.vention.medfast.patient.mapper;

import com.vention.medfast.patient.dto.PatientCreateDto;
import com.vention.medfast.patient.dto.PatientResponseDto;
import com.vention.medfast.patient.model.Patient;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PatientMapper{

    Patient toEntity(PatientCreateDto dto);

    PatientResponseDto toDto(Patient entity);

}
