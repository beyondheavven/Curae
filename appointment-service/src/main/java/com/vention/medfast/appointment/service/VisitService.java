package com.vention.medfast.appointment.service;

import com.vention.medfast.appointment.dto.StatusStatisticDto;
import com.vention.medfast.appointment.dto.VisitCreateDto;
import com.vention.medfast.appointment.dto.VisitListItemDto;
import com.vention.medfast.appointment.model.VisitType;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VisitService {

	Long create(Long patientId, VisitCreateDto dto);

	Page<VisitListItemDto> findAllForPatient(Long patientId, VisitType type, Pageable pageable);

	List<StatusStatisticDto> getStatusStatisticsByClinic();

	List<StatusStatisticDto> getStatusStatisticsByDoctor();

}
