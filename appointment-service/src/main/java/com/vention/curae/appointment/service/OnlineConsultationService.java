package com.vention.curae.appointment.service;

import com.vention.curae.appointment.dto.CreateOnlineConsultationRequest;
import com.vention.curae.appointment.dto.OnlineConsultationDetails;
import com.vention.curae.appointment.dto.UpdateOnlineConsultationRequest;

public interface OnlineConsultationService {

	OnlineConsultationDetails getDetails(Long consultationId);

	Long create(CreateOnlineConsultationRequest dto);

	void update(UpdateOnlineConsultationRequest dto);

	void deleteById(Long consultationId);

}
