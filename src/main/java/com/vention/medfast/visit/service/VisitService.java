package com.vention.medfast.visit.service;

import com.vention.medfast.visit.dto.VisitCreateDto;
import com.vention.medfast.visit.dto.VisitResponseDto;
import com.vention.medfast.visit.model.VisitType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VisitService {

	Long create(Long patientId, VisitCreateDto dto);

	Page<VisitResponseDto> findAllForPatient(VisitType type, Pageable pageable);

}
