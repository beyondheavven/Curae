package com.vention.curae.appointment.service;

import com.vention.curae.appointment.dto.ReferralCreateDto;
import com.vention.curae.appointment.dto.ReferralResponseDto;
import com.vention.curae.appointment.model.Referral;
import java.util.List;

public interface ReferralService {

	Long create(ReferralCreateDto dto);

	List<ReferralResponseDto> getActiveByPatient(Long patientId);

	Referral getActiveById(Long referralId);

	void markUsed(Long referralId);

}
