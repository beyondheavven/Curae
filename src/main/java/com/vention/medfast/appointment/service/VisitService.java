package com.vention.medfast.appointment.service;

import com.vention.medfast.appointment.dto.VisitCreateDto;
import com.vention.medfast.appointment.dto.VisitResponseDto;
import com.vention.medfast.appointment.model.VisitType;
import com.vention.medfast.statistics.dto.response.AppointmentStatusCounts;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VisitService {

	Long create(Long patientId, VisitCreateDto dto);

	Page<VisitResponseDto> findAllForPatient(VisitType type, Pageable pageable);

	AppointmentStatusCounts getStatusStatisticsByClinic(Long clinicId, LocalDate startDate, LocalDate endDate);

	AppointmentStatusCounts getStatusStatisticsByDoctor(Long doctorId, LocalDate start, LocalDate end);

}
