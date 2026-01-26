package com.vention.medfast.appointment.service;

import com.vention.medfast.appointment.dto.AppointmentCreateDto;
import com.vention.medfast.appointment.dto.AppointmentCreateWithLabsDto;
import com.vention.medfast.appointment.dto.FirstAppointmentCreateDto;
import com.vention.medfast.appointment.dto.PatientLabHistoryDTO;
import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.appointment.model.AppointmentType;
import com.vention.medfast.statistics.dto.response.AppointmentStatusCounts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface AppointmentService {

	Long createFirst(FirstAppointmentCreateDto dto);

	Long createDraft(AppointmentCreateDto dto);

	void confirm(Long appointmentId);

	Appointment getConfirmed(Long appointmentId);

	void createWithLabs(AppointmentCreateWithLabsDto dto);

	Page<PatientLabHistoryDTO> getPatientLabHistory(Long userId, Pageable pageable);

	AppointmentStatusCounts getAllStatusStatistics(LocalDate start, LocalDate end);

	AppointmentStatusCounts getStatusStatisticsByType(LocalDate start, LocalDate end, AppointmentType type);

}
