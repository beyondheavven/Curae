package com.vention.medfast.patient.service;


import com.vention.medfast.patient.dto.PatientCreateDto;
import com.vention.medfast.patient.dto.PatientResponseDto;

public interface PatientService {

    PatientResponseDto create( PatientCreateDto patientCreateDto);

    PatientResponseDto getPatientById(Long id);
}
