package com.vention.medfast.appointment.service;

import com.vention.medfast.appointment.dto.AppointmentCreateWithLabsDto;
import com.vention.medfast.appointment.dto.PatientLabHistoryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AppointmentService {

	Page<PatientLabHistoryDTO> getPatientLabHistory(Long userId, Pageable pageable);

	void createWithLabs(AppointmentCreateWithLabsDto appointmentCreateWithLabsDto);

}
