package com.vention.medfast.appointment.service;

import com.vention.medfast.appointment.dto.AppointmentCreateDto;
import com.vention.medfast.appointment.dto.AppointmentUpdateDto;
import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.appointment.model.AppointmentStatus;
import com.vention.medfast.appointment.model.AppointmentType;
import com.vention.medfast.statistics.dto.response.AppointmentStatusCounts;
import java.time.LocalDate;
import java.util.List;

public interface AppointmentService {

	Long create(AppointmentCreateDto dto);

	void confirm(Long appointmentId);

	Appointment getConfirmed(Long appointmentId);

	AppointmentStatusCounts getAllStatusStatistics(LocalDate start, LocalDate end);

	AppointmentStatusCounts getStatusStatisticsByType(LocalDate start, LocalDate end, AppointmentType type);

	Appointment findAppointment(Long appointmentId);

	void cancelAppointment(Long appointmentId);

	Long update(AppointmentUpdateDto dto, Long id);

	boolean existsByPatientIdDoctorIdAndStatus(Long patientId, Long doctorId, AppointmentStatus status);

}
