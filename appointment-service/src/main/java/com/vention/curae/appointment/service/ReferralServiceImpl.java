package com.vention.curae.appointment.service;

import com.vention.curae.appointment.dto.ReferralCreateDto;
import com.vention.curae.appointment.dto.ReferralResponseDto;
import com.vention.curae.appointment.model.Referral;
import com.vention.curae.appointment.model.ReferralStatus;
import com.vention.curae.appointment.repository.ReferralRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ReferralServiceImpl implements ReferralService {

	private final ReferralRepository referralRepository;

	@Override
	@Transactional
	public Long create(ReferralCreateDto dto) {
		Referral referral = new Referral();
		referral.setPatientId(dto.patientId());
		referral.setIssuedBy(currentUserId());
		referral.setServiceId(dto.serviceId());
		referral.setAppointmentType(dto.appointmentType());
		referral.setExpiresAt(dto.expiresAt() == null ? LocalDate.now().plusMonths(3) : dto.expiresAt());
		referral.setStatus(ReferralStatus.ACTIVE);
		return referralRepository.save(referral).getId();
	}

	@Override
	@Transactional(readOnly = true)
	public List<ReferralResponseDto> getActiveByPatient(Long patientId) {
		return referralRepository.findByPatientIdAndStatus(patientId, ReferralStatus.ACTIVE)
			.stream()
			.map(referral -> new ReferralResponseDto(referral.getId(), referral.getServiceId(), referral.getExpiresAt(),
					referral.getStatus()))
			.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public Referral getActiveById(Long referralId) {
		Referral referral = referralRepository.findById(referralId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Referral not found"));
		if (referral.getStatus() != ReferralStatus.ACTIVE) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Referral is not active");
		}
		return referral;
	}

	@Override
	@Transactional
	public void markUsed(Long referralId) {
		Referral referral = getActiveById(referralId);
		referral.setStatus(ReferralStatus.USED);
		referralRepository.save(referral);
	}

	private Long currentUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
		}
		try {
			return Long.valueOf(authentication.getName());
		}
		catch (NumberFormatException ex) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user id");
		}
	}

}
