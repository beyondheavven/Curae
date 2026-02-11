package com.vention.medfast.appointment.service;

import com.vention.medfast.appointment.dto.VisitCreateDto;
import com.vention.medfast.appointment.dto.VisitListItemDto;
import com.vention.medfast.appointment.model.VisitType;
import com.vention.medfast.statistics.dto.response.AppointmentStatusCounts;
import java.time.LocalDate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface VisitService {

	Long create(Long patientId, VisitCreateDto dto);

	Slice<VisitListItemDto> findAllForPatient(VisitType type, Pageable pageable);

	AppointmentStatusCounts getStatusStatisticsByClinic(Long clinicId, LocalDate startDate, LocalDate endDate);

	AppointmentStatusCounts getStatusStatisticsByDoctor(Long doctorId, LocalDate start, LocalDate end);

}
