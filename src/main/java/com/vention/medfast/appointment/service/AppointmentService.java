package com.vention.medfast.appointment.service;

import com.vention.medfast.appointment.dto.AppointmentCreateDto;
import com.vention.medfast.appointment.dto.FirstAppointmentCreateDto;
import com.vention.medfast.appointment.model.Appointment;

public interface AppointmentService {
    Long createFirst(FirstAppointmentCreateDto dto);
    Long createDraft(AppointmentCreateDto dto);
    void confirm(Long appointmentId);
    Appointment getConfirmed(Long appointmentId);
}
