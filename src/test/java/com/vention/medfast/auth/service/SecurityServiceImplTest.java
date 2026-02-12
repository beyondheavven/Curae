package com.vention.medfast.auth.service;

import com.vention.medfast.appointment.referral.service.ReferralService;
import com.vention.medfast.appointment.service.AppointmentService;
import com.vention.medfast.auth.core.CustomUserDetails;
import com.vention.medfast.notification.service.NotificationsService;
import com.vention.medfast.user.model.RoleType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityServiceImplTest {

	@Mock
	private AppointmentService appointmentService;

	@Mock
	private ReferralService referralService;

	@Mock
	private NotificationsService notificationsService;

	@InjectMocks
	private SecurityServiceImpl securityService;

	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
	}

	private void setupSecurityContext(CustomUserDetails principal) {
		Authentication auth = mock(Authentication.class);
		SecurityContext context = mock(SecurityContext.class);
		when(context.getAuthentication()).thenReturn(auth);
		when(auth.getPrincipal()).thenReturn(principal);
		SecurityContextHolder.setContext(context);
	}

	@Test
	void shouldReturnTrueWhenPatientIsOwnerOfAppointment() {
		Long appId = 1L;
		Long patientId = 10L;
		CustomUserDetails principal = new CustomUserDetails(1L, "p@t.com", "h", RoleType.PATIENT, patientId, null);
		setupSecurityContext(principal);

		when(appointmentService.isOwner(appId, patientId)).thenReturn(true);

		boolean result = securityService.isAppointmentAvailable(appId);

		assertThat(result).isTrue();
	}

	@Test
	void shouldReturnTrueWhenPatientAccessesOwnAppointmentExtendedCheck() {
		Long appId = 1L;
		CustomUserDetails principal = new CustomUserDetails(1L, "p@t.com", "h", RoleType.PATIENT, 10L, null);
		setupSecurityContext(principal);

		when(appointmentService.isOwnerExtended(appId, principal)).thenReturn(true);

		boolean result = securityService.isAppointmentAvailableExtended(appId);

		assertThat(result).isTrue();
	}

	@Test
	void shouldReturnTrueWhenDoctorAccessesAssignedAppointmentExtendedCheck() {
		Long appId = 1L;
		CustomUserDetails principal = new CustomUserDetails(2L, "d@t.com", "h", RoleType.DOCTOR, null, 20L);
		setupSecurityContext(principal);

		when(appointmentService.isOwnerExtended(appId, principal)).thenReturn(true);

		boolean result = securityService.isAppointmentAvailableExtended(appId);

		assertThat(result).isTrue();
	}

	@Test
	void shouldReturnTrueWhenReferralIdIsNull() {
		assertThat(securityService.canUseReferral(null)).isTrue();
	}

	@Test
	void shouldReturnTrueWhenPatientIsOwnerOfReferral() {
		Long refId = 100L;
		Long patientId = 10L;
		CustomUserDetails principal = new CustomUserDetails(1L, "p@t.com", "h", RoleType.PATIENT, patientId, null);
		setupSecurityContext(principal);

		when(referralService.isOwner(refId, patientId)).thenReturn(true);

		boolean result = securityService.canUseReferral(refId);

		assertThat(result).isTrue();
	}

	@Test
	void shouldReturnTrueWhenPatientIsOwnerOfNotification() {
		Long noteId = 50L;
		Long patientId = 10L;
		CustomUserDetails principal = new CustomUserDetails(1L, "p@t.com", "h", RoleType.PATIENT, patientId, null);
		setupSecurityContext(principal);

		when(notificationsService.isOwner(noteId, patientId)).thenReturn(true);

		boolean result = securityService.isNotificationAvailable(noteId);

		assertThat(result).isTrue();
	}

	@Test
	void shouldThrowAccessDeniedExceptionWhenAuthenticationIsMissing() {
		assertThatThrownBy(() -> securityService.isAppointmentAvailable(1L)).isInstanceOf(AccessDeniedException.class)
			.hasMessageContaining("User is not authenticated");
	}

	@Test
	void shouldReturnFalseWhenServiceReturnsFalse() {
		Long appId = 1L;
		CustomUserDetails principal = new CustomUserDetails(1L, "p@t.com", "h", RoleType.PATIENT, 10L, null);
		setupSecurityContext(principal);

		when(appointmentService.isOwner(appId, 10L)).thenReturn(false);

		boolean result = securityService.isAppointmentAvailable(appId);

		assertThat(result).isFalse();
	}

}
