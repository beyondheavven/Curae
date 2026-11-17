package com.vention.medfast.appointment.security;

import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.appointment.model.Referral;
import com.vention.medfast.appointment.repository.AppointmentRepository;
import com.vention.medfast.appointment.repository.OnlineConsultationRepository;
import com.vention.medfast.appointment.repository.ReferralRepository;
import com.vention.medfast.appointment.repository.VisitRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service("securityService")
@RequiredArgsConstructor
public class AppointmentSecurityService {

	private final AppointmentRepository appointmentRepository;

	private final ReferralRepository referralRepository;

	private final VisitRepository visitRepository;

	private final OnlineConsultationRepository onlineConsultationRepository;

	public boolean isAppointmentAvailable(Long appointmentId) {
		Optional<Appointment> appointment = appointmentRepository.findById(appointmentId);
		Long patientId = currentPatientId();
		return patientId != null && appointment.map(a -> patientId.equals(a.getPatientId())).orElse(false);
	}

	public boolean isAppointmentAvailableExtended(Long appointmentId) {
		if (currentUserHasAuthority("ROLE_ADMIN")) {
			return true;
		}
		if (isAppointmentAvailable(appointmentId)) {
			return true;
		}
		Long userId = currentUserId();
		if (userId == null) {
			return false;
		}
		return visitRepository.findByAppointmentId(appointmentId).map(v -> userId.equals(v.getDoctorId())).orElse(false)
				|| onlineConsultationRepository.findByAppointmentId(appointmentId)
					.map(c -> userId.equals(c.getDoctorId()))
					.orElse(false)
				|| appointmentRepository.findById(appointmentId)
					.map(a -> a.getReferralId() != null && referralRepository.findById(a.getReferralId())
						.map(r -> userId.equals(r.getIssuedBy()))
						.orElse(false))
					.orElse(false);
	}

	public boolean canUseReferral(Long referralId) {
		Long patientId = currentPatientId();
		if (patientId == null) {
			return false;
		}
		return referralRepository.findById(referralId).map(r -> patientId.equals(r.getPatientId())).orElse(false);
	}

	public Long currentPatientId() {
		if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
			String header = attributes.getRequest().getHeader("X-Patient-Id");
			if (header != null && !header.isBlank()) {
				try {
					return Long.valueOf(header);
				}
				catch (NumberFormatException ignored) {
					return currentUserId();
				}
			}
		}
		return currentUserId();
	}

	private Long currentUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication.getName() == null) {
			return null;
		}
		try {
			return Long.valueOf(authentication.getName());
		}
		catch (NumberFormatException ex) {
			return null;
		}
	}

	private boolean currentUserHasAuthority(String authority) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication != null && authentication.getAuthorities()
			.stream()
			.anyMatch(granted -> authority.equals(granted.getAuthority()));
	}

}
