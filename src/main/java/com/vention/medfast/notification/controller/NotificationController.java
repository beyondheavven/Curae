package com.vention.medfast.notification.controller;

import com.vention.medfast.auth.core.CustomUserDetails;
import com.vention.medfast.api.response.ErrorResponseDto;
import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.notification.dto.NotificationDetailsDto;
import com.vention.medfast.notification.dto.NotificationDto;
import com.vention.medfast.notification.service.NotificationsService;
import com.vention.medfast.wrapper.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping(ApiPaths.NOTIFICATION_ENDPOINT)
@Tag(name = "Notifications", description = "Endpoints for patient notifications")
public class NotificationController {

	private final NotificationsService notificationService;

	@Operation(summary = "Get notifications",
			description = "Returns paginated notifications for the authenticated patient sorted by creation time.")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully",
			content = @Content(mediaType = "application/json",
					schema = @Schema(implementation = NotificationDto.class))) })
	@GetMapping
	public ResponseWrapper<Page<NotificationDto>> getNotifications(@AuthenticationPrincipal CustomUserDetails user,
			@ParameterObject @PageableDefault(size = 10, sort = "createdAt",
					direction = Sort.Direction.DESC) Pageable pageable) {
		return ResponseWrapper.ok(notificationService.getPatientNotifications(user.getId(), pageable));
	}

	@Operation(summary = "Get notification details",
			description = "Retrieves a single notification with detailed information by its id.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Notification details retrieved",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = NotificationDetailsDto.class))),
			@ApiResponse(responseCode = "404", description = "Notification not found",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponseDto.class))) })
	@PreAuthorize("hasRole('ADMIN') or (hasRole('PATIENT') && @securityService.isNotificationAvailable(#id))")
	@GetMapping("/{id}")
	public ResponseWrapper<NotificationDetailsDto> getNotificationDetails(
			@Parameter(description = "ID of the notification") @PathVariable Long id) {
		return ResponseWrapper.ok(notificationService.getPatientNotificationDetails(id));
	}

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Mark notification as read",
			description = "Updates the notification status to READ for the authenticated patient.")
	@ApiResponses({ @ApiResponse(responseCode = "204", description = "Notification marked as read"),
			@ApiResponse(responseCode = "403", description = "User not allowed to modify this notification",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponseDto.class))),
			@ApiResponse(responseCode = "404", description = "Notification not found",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponseDto.class))) })
	@PreAuthorize("hasRole('ADMIN') or (hasRole('PATIENT') && @securityService.isNotificationAvailable(#id))")
	@PatchMapping("/{id}/read")
	public void markNotificationAsRead(@Parameter(description = "ID of the notification") @PathVariable Long id,
			@AuthenticationPrincipal CustomUserDetails user) {
		notificationService.markAsRead(id, user.getId());
	}

}
