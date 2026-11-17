package com.vention.medfast.notification.controller;

import com.vention.medfast.notification.dto.NotificationDetailsDto;
import com.vention.medfast.notification.dto.NotificationDto;
import com.vention.medfast.notification.service.NotificationsService;
import com.vention.medfast.wrapper.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Endpoints for patient notifications")
public class NotificationController {

	private final NotificationsService notificationService;

	@PreAuthorize("hasAnyRole('ADMIN','PATIENT')")
	@GetMapping
	@Operation(summary = "Get notifications")
	public ResponseWrapper<Page<NotificationDto>> getNotifications(
			@RequestHeader(value = "X-Patient-Id", required = false) Long patientId, Principal principal,
			@ParameterObject @PageableDefault(size = 10, sort = "createdAt",
					direction = Sort.Direction.DESC) Pageable pageable) {
		return ResponseWrapper
			.ok(notificationService.getPatientNotifications(resolvePatientId(patientId, principal), pageable));
	}

	@PreAuthorize("hasAnyRole('ADMIN','PATIENT')")
	@GetMapping("/{id}")
	@Operation(summary = "Get notification details")
	public ResponseWrapper<NotificationDetailsDto> getNotificationDetails(@PathVariable Long id) {
		return ResponseWrapper.ok(notificationService.getNotificationDetails(id));
	}

	@PreAuthorize("hasAnyRole('ADMIN','PATIENT')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PatchMapping("/{id}/read")
	@Operation(summary = "Mark notification as read")
	public void markNotificationAsRead(@PathVariable Long id,
			@RequestHeader(value = "X-Patient-Id", required = false) Long patientId, Principal principal) {
		notificationService.markAsRead(id, resolvePatientId(patientId, principal));
	}

	private Long resolvePatientId(Long patientId, Principal principal) {
		if (patientId != null) {
			return patientId;
		}
		if (principal == null) {
			throw new org.springframework.security.access.AccessDeniedException("Patient id is required");
		}
		return Long.parseLong(principal.getName());
	}

}
