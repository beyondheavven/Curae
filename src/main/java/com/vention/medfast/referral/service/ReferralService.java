package com.vention.medfast.referral.service;

import com.vention.medfast.referral.dto.ReferralCreateDto;
import com.vention.medfast.referral.dto.ReferralResponseDto;
import com.vention.medfast.referral.model.Referral;

import java.util.List;

public interface ReferralService {

	List<ReferralResponseDto> getActiveByPatient(Long patientId);

	Referral getActiveById(Long referralId);

	Referral markUsed(Referral referral);

	Long create(ReferralCreateDto dto);

}
