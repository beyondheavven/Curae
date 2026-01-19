package com.vention.medfast.appointment.referral.service;

import com.vention.medfast.appointment.referral.dto.ReferralCreateDto;
import com.vention.medfast.appointment.referral.dto.ReferralResponseDto;
import com.vention.medfast.appointment.referral.model.Referral;

import java.util.List;

public interface ReferralService {
    List<ReferralResponseDto> getActiveByPatient(Long patientId);
    Referral getActiveById(Long referralId);
    Referral markUsed(Referral referral);
    Long create(ReferralCreateDto dto);
}
