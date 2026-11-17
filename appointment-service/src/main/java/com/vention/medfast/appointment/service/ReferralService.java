package com.vention.medfast.appointment.service;

import com.vention.medfast.appointment.dto.ReferralCreateDto;
import com.vention.medfast.appointment.dto.ReferralResponseDto;
import com.vention.medfast.appointment.model.Referral;
import java.util.List;

public interface ReferralService {

	Long create(ReferralCreateDto dto);

	List<ReferralResponseDto> getActiveByPatient(Long patientId);

	Referral getActiveById(Long referralId);

	void markUsed(Long referralId);

}
