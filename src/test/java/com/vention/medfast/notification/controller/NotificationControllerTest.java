package com.vention.medfast.notification.controller;

import com.vention.medfast.auth.core.CustomUserDetails;
import com.vention.medfast.notification.dto.NotificationDetailsDto;
import com.vention.medfast.notification.dto.NotificationDto;
import com.vention.medfast.notification.model.NotificationStatus;
import com.vention.medfast.notification.model.NotificationType;
import com.vention.medfast.notification.service.NotificationsService;
import com.vention.medfast.user.model.RoleType;
import com.vention.medfast.wrapper.ResponseWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

	@Mock
	private NotificationsService notificationsService;

	private NotificationController controller;

	@BeforeEach
	void setUp() {
		controller = new NotificationController(notificationsService);
	}

	@Test
	void getNotificationsReturnsPageWrappedWithOkStatus() {
		CustomUserDetails user = new CustomUserDetails(1L, "user@mail.com", "pwd", RoleType.PATIENT);
		Pageable pageable = PageRequest.of(0, 10);
		Page<NotificationDto> page = new PageImpl<>(List.of(new NotificationDto(2L, 3L,
				NotificationType.LAB_RESULT_READY, NotificationStatus.UNREAD, Instant.now())));
		when(notificationsService.getPatientNotifications(user.getId(), pageable)).thenReturn(page);

		ResponseWrapper<Page<NotificationDto>> response = controller.getNotifications(user, pageable);

		assertThat(response.getData()).isEqualTo(page);
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
		verify(notificationsService).getPatientNotifications(user.getId(), pageable);
	}

	@Test
	void getNotificationDetailsReturnsWrapperWithOkStatus() {
		Long id = 5L;
		NotificationDetailsDto details = new NotificationDetailsDto(id, 7L, NotificationType.APPOINTMENT_CANCELLED,
				NotificationStatus.UNREAD, Instant.now());
		when(notificationsService.getPatientNotificationDetails(id)).thenReturn(details);

		ResponseWrapper<NotificationDetailsDto> response = controller.getNotificationDetails(id);

		assertThat(response.getData()).isEqualTo(details);
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
		verify(notificationsService).getPatientNotificationDetails(id);
	}

	@Test
	void markNotificationAsReadDelegatesToService() {
		Long id = 9L;
		CustomUserDetails user = new CustomUserDetails(4L, "patient@mail.com", "pwd", RoleType.PATIENT);

		controller.markNotificationAsRead(id, user);

		verify(notificationsService).markAsRead(id, user.getId());
	}

}
