package com.vention.medfast.appointment.referral.service;

import com.vention.medfast.api.exception.AppointmentException;
import com.vention.medfast.appointment.referral.dto.ReferralCreateDto;
import com.vention.medfast.appointment.referral.dto.ReferralResponseDto;
import com.vention.medfast.appointment.referral.mapper.ReferralMapper;
import com.vention.medfast.appointment.referral.model.Referral;
import com.vention.medfast.appointment.referral.model.ReferralStatus;
import com.vention.medfast.appointment.referral.repository.ReferralRepository;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.doctor.service.DoctorService;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.patient.service.PatientService;
import com.vention.medfast.service.model.ProvidedService;
import com.vention.medfast.service.service.ProvidedServiceService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReferralServiceImpl implements ReferralService {

    private final ReferralRepository referralRepository;
    private final ReferralMapper referralMapper;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final ProvidedServiceService providedServiceService;

    @Override
    @Transactional(readOnly = true)
    public List<ReferralResponseDto> getActiveByPatient(Long patientId) {
        return referralRepository.findActiveByPatient(patientId, ReferralStatus.ACTIVE, LocalDate.now())
            .stream()
            .map(referralMapper::toDto)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Referral getActiveById(Long referralId) {
        Referral referral = referralRepository.findById(referralId)
            .orElseThrow(() -> new EntityNotFoundException("Referral not found"));
        if (referral.getStatus() != ReferralStatus.ACTIVE) {
            throw new AppointmentException("Referral is not active");
        }
        if (isExpired(referral)) {
            throw new AppointmentException("Referral expired");
        }
        return referral;
    }

    @Override
    @Transactional
    public Referral markUsed(Referral referral) {
        if (referral.getStatus() != ReferralStatus.ACTIVE) {
            throw new AppointmentException("Only active referral can be used");
        }
        referral.setStatus(ReferralStatus.USED);
        return referral;
    }

    @Override
    @Transactional
    public Long create(ReferralCreateDto dto) {
        Patient patient = patientService.getById(dto.patientId());
        ProvidedService service =
            providedServiceService.getProvidedServiceById(dto.serviceId());
        Doctor doctor = doctorService.getCurrentDoctor();
        Referral referral =
            referralMapper.toEntity(dto, patient, doctor, service);
        return referralRepository.save(referral).getId();
    }

    private boolean isExpired(Referral referral) {
        return referral.getExpiresAt().isBefore(LocalDate.now());
    }
}
