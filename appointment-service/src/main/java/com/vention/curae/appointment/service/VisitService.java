package com.vention.curae.appointment.service;

import com.vention.curae.appointment.dto.StatusStatisticDto;
import com.vention.curae.appointment.dto.VisitCreateDto;
import com.vention.curae.appointment.dto.VisitListItemDto;
import com.vention.curae.appointment.model.VisitType;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VisitService {

	Long create(Long patientId, VisitCreateDto dto);

	Page<VisitListItemDto> findAllForPatient(Long patientId, VisitType type, Pageable pageable);

	List<StatusStatisticDto> getStatusStatisticsByClinic();

	List<StatusStatisticDto> getStatusStatisticsByDoctor();

}
