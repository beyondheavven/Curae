package com.vention.curae.appointment.service;

import com.vention.curae.appointment.dto.AppointmentCreateDto;
import com.vention.curae.appointment.dto.AppointmentUpdateDto;
import com.vention.curae.appointment.model.Appointment;
import com.vention.curae.appointment.model.AppointmentStatus;

public interface AppointmentService {

	Long create(AppointmentCreateDto dto);

	void confirm(Long appointmentId);

	void cancelAppointment(Long appointmentId);

	void update(Long appointmentId, AppointmentUpdateDto dto);

	Appointment getById(Long appointmentId);

	boolean existsByPatientIdDoctorIdAndStatus(Long patientId, Long doctorId, AppointmentStatus status);

}
