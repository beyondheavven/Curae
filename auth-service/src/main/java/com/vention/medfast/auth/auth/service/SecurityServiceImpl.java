package com.vention.medfast.auth.auth.service;

import com.vention.medfast.appointment.referral.service.ReferralService;
import com.vention.medfast.appointment.service.AppointmentService;
import com.vention.medfast.auth.core.CustomUserDetails;
import com.vention.medfast.notification.service.NotificationsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("securityService")
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

	private final AppointmentService appointmentService;

	private final ReferralService referralService;

	private final NotificationsService notificationsService;

	@Override
	public boolean isAppointmentAvailable(Long appointmentId) {
		return appointmentService.isOwner(appointmentId, getPrincipal().getPatientId());
	}

	@Override
	public boolean isAppointmentAvailableExtended(Long appointmentId) {
		return appointmentService.isOwnerExtended(appointmentId, getPrincipal());
	}

	@Override
	public boolean canUseReferral(Long referralId) {
		if (referralId == null)
			return true;
		return referralService.isOwner(referralId, getPrincipal().getPatientId());
	}

	@Override
	public boolean isNotificationAvailable(Long notificationId) {
		return notificationsService.isOwner(notificationId, getPrincipal().getPatientId());
	}

	private CustomUserDetails getPrincipal() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails principal)) {
			throw new AccessDeniedException("User is not authenticated");
		}
		return principal;
	}

}
