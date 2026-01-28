package com.vention.medfast.appointment.service;

import com.vention.medfast.appointment.dto.CreateOnlineConsultationRequest;
import com.vention.medfast.appointment.dto.OnlineConsultationDetails;
import com.vention.medfast.appointment.dto.UpdateOnlineConsultationRequest;

public interface OnlineConsultationService {

	OnlineConsultationDetails getDetails(Long consultationId);

	void create(CreateOnlineConsultationRequest createOnlineConsultationRequest);

	void deleteById(Long consultationId);

	void update(UpdateOnlineConsultationRequest updateOnlineConsultationRequest);

}
