package com.vention.medfast.appointment.service;

import com.vention.medfast.appointment.dto.VisitCreateDto;
import com.vention.medfast.appointment.dto.VisitResponseDto;
import com.vention.medfast.appointment.model.VisitType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VisitService {

	Long create(Long patientId, VisitCreateDto dto);

	Page<VisitResponseDto> findAllForPatient(VisitType type, Pageable pageable);

}
