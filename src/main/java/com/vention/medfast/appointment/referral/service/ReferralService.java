package com.vention.medfast.appointment.referral.service;

import com.vention.medfast.appointment.dto.PrescriptionCreateDto;
import com.vention.medfast.appointment.referral.model.Referral;

import java.util.List;

public interface ReferralService {
    List<Referral> getActiveByPatient(Long patientId);
    Referral getActiveById(Long referralId);
    Referral markUsed(Referral referral);
    Long create(PrescriptionCreateDto dto);
}
