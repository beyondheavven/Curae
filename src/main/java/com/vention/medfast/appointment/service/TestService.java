package com.vention.medfast.appointment.service;

import com.vention.medfast.appointment.dto.PatientLabHistoryDTO;
import com.vention.medfast.appointment.dto.TestCreateWithLabsDto;
import com.vention.medfast.statistics.dto.response.AppointmentStatusCounts;
import jakarta.validation.Valid;
import java.time.LocalDate;
import com.vention.medfast.wrapper.CachedPage;
import org.springframework.data.domain.Pageable;

public interface TestService {

	CachedPage<PatientLabHistoryDTO> getPatientLabHistory(Long userId, Pageable pageable);

	void createWithLabs(@Valid TestCreateWithLabsDto request);

	AppointmentStatusCounts getStatusStatisticsByClinic(Long clinicId, LocalDate startDate, LocalDate endDate);

}
