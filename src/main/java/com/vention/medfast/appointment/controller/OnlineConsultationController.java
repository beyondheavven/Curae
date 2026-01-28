package com.vention.medfast.appointment.controller;

import com.vention.medfast.appointment.dto.CreateOnlineConsultationRequest;
import com.vention.medfast.appointment.dto.OnlineConsultationDetails;
import com.vention.medfast.appointment.dto.UpdateOnlineConsultationRequest;
import com.vention.medfast.appointment.service.OnlineConsultationService;
import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.wrapper.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.ONLINE_CONSULTATION_ENDPOINT)
@AllArgsConstructor
@Tag(name = "Online consultations", description = "CRUD endpoints for online consultations")
public class OnlineConsultationController {

	private final OnlineConsultationService onlineConsultationService;

	@Operation(summary = "Get online consultation by id")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Found",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = OnlineConsultationDetails.class))),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content) })
	@GetMapping("/{id}")
	public ResponseWrapper<OnlineConsultationDetails> getDetails(
			@Parameter(description = "Consultation ID", required = true) @PathVariable("id") Long consultationId) {
		return ResponseWrapper.ok(onlineConsultationService.getDetails(consultationId));
	}

	@Operation(summary = "Create online consultation")
	@ApiResponses({ @ApiResponse(responseCode = "201", description = "Created"),
			@ApiResponse(responseCode = "400", description = "Validation error",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ResponseWrapper.class))) })
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public void create(@Valid @RequestBody CreateOnlineConsultationRequest createOnlineConsultationRequest) {
		onlineConsultationService.create(createOnlineConsultationRequest);
	}

	@Operation(summary = "Update online consultation")
	@ApiResponses({ @ApiResponse(responseCode = "204", description = "Updated"),
			@ApiResponse(responseCode = "400", description = "Validation error",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ResponseWrapper.class))) })
	@PatchMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void update(@Valid @RequestBody UpdateOnlineConsultationRequest updateOnlineConsultationRequest) {
		onlineConsultationService.update(updateOnlineConsultationRequest);
	}

	@Operation(summary = "Delete online consultation")
	@ApiResponses({ @ApiResponse(responseCode = "204", description = "Deleted"),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content) })
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(
			@Parameter(description = "Consultation ID", required = true) @PathVariable("id") Long consultationId) {
		onlineConsultationService.deleteById(consultationId);
	}

}
