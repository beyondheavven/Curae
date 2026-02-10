package com.vention.medfast.feedback.clinic.controller;

import com.vention.medfast.api.response.ErrorResponseDto;
import com.vention.medfast.auth.core.CustomUserDetails;
import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.feedback.clinic.dto.ClinicFeedbackCreateDto;
import com.vention.medfast.feedback.clinic.dto.ClinicFeedbackSummaryDto;
import com.vention.medfast.feedback.clinic.service.ClinicFeedbackService;
import com.vention.medfast.specialization.dto.SpecializationResponseDto;
import com.vention.medfast.wrapper.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.FEEDBACK_ENDPOINT + ApiPaths.CLINIC_ENDPOINT)
@RequiredArgsConstructor
@Tag(name = "Feedbacks", description = "Endpoints for managing clinic feedbacks")
public class ClinicFeedbackController {

	private final ClinicFeedbackService clinicFeedbackService;

	@Operation(summary = "Get feedback summary")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successfully retrieved feedback summary",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = SpecializationResponseDto.class))),
			@ApiResponse(responseCode = "404", description = "Clinic not found",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponseDto.class))) })
	@GetMapping("/{clinicId}")
	public ResponseWrapper<ClinicFeedbackSummaryDto> getSummary(@PathVariable Long clinicId,
			@ParameterObject Pageable pageable) {
		return ResponseWrapper.ok(clinicFeedbackService.getFeedbackSummaryByClinicId(clinicId, pageable));
	}

	@Operation(summary = "Create feedback for clinic")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Successfully created",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = SpecializationResponseDto.class))),
			@ApiResponse(responseCode = "400", description = "Validation error",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponseDto.class))) })
	@PreAuthorize("hasRole('PATIENT')")
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseWrapper<Long> create(@AuthenticationPrincipal CustomUserDetails userDetails,
			@Valid @RequestBody ClinicFeedbackCreateDto dto) {
		long id = clinicFeedbackService.create(userDetails.getUsername(), dto);
		return ResponseWrapper.withStatus(HttpStatus.CREATED, id);
	}

}
