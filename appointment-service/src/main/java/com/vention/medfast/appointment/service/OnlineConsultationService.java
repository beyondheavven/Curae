package com.vention.medfast.appointment.service;

import com.vention.medfast.appointment.dto.CreateOnlineConsultationRequest;
import com.vention.medfast.appointment.dto.OnlineConsultationDetails;
import com.vention.medfast.appointment.dto.UpdateOnlineConsultationRequest;

public interface OnlineConsultationService {

	OnlineConsultationDetails getDetails(Long consultationId);

	Long create(CreateOnlineConsultationRequest dto);

	void update(UpdateOnlineConsultationRequest dto);

	void deleteById(Long consultationId);

}
