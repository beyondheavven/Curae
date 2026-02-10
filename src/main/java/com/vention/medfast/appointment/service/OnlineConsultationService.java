package com.vention.medfast.appointment.service;

import com.vention.medfast.appointment.dto.CreateOnlineConsultationRequest;
import com.vention.medfast.appointment.dto.OnlineConsultationDetails;
import com.vention.medfast.appointment.dto.UpdateOnlineConsultationRequest;
import com.vention.medfast.statistics.dto.response.AppointmentStatusCounts;
import java.time.LocalDate;

public interface OnlineConsultationService {

	OnlineConsultationDetails getDetails(Long consultationId);

	void create(CreateOnlineConsultationRequest createOnlineConsultationRequest);

	void deleteById(Long consultationId);

	void update(UpdateOnlineConsultationRequest updateOnlineConsultationRequest);

	AppointmentStatusCounts getStatusStatisticsByClinic(Long clinicId, LocalDate startDate, LocalDate endDate);

	AppointmentStatusCounts getStatusStatisticsByDoctor(Long doctorId, LocalDate start, LocalDate end);

}
