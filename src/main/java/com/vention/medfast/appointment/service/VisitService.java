package com.vention.medfast.appointment.service;

import com.vention.medfast.appointment.dto.VisitCreateDto;
import com.vention.medfast.appointment.dto.VisitListItemDto;
import com.vention.medfast.appointment.model.VisitType;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Pageable;

public interface VisitService {

	Long create(Long patientId, VisitCreateDto dto);

	Slice<VisitListItemDto> findAllForPatient(VisitType type, Pageable pageable);

}
