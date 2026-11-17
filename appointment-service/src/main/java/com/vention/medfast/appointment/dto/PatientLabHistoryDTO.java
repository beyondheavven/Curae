package com.vention.medfast.appointment.dto;

import java.time.LocalDate;
import java.util.List;

public record PatientLabHistoryDTO(Long appointmentId, LocalDate date, List<LabResultDTO> tests) {
}
