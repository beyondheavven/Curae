package com.vention.medfast.appointment.controller;

import com.vention.medfast.appointment.dto.VisitCreateDto;
import com.vention.medfast.appointment.dto.VisitListItemDto;
import com.vention.medfast.appointment.model.VisitType;
import com.vention.medfast.appointment.security.AppointmentSecurityService;
import com.vention.medfast.appointment.service.VisitService;
import com.vention.medfast.wrapper.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/visits")
@RequiredArgsConstructor
@Tag(name = "Visits", description = "Endpoints for managing visits")
public class VisitController {

	private final VisitService visitService;

	private final AppointmentSecurityService securityService;

	@PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Create visit")
	public ResponseWrapper<Long> create(@Valid @RequestBody VisitCreateDto dto,
			@RequestParam(name = "patientId") Long patientId) {
		return ResponseWrapper.withStatus(HttpStatus.CREATED, visitService.create(patientId, dto));
	}

	@PreAuthorize("hasRole('PATIENT')")
	@GetMapping
	@Operation(summary = "Get patient visits")
	public ResponseWrapper<Page<VisitListItemDto>> getAll(
			@RequestParam(name = "type", defaultValue = "UPCOMING") VisitType type,
			@ParameterObject Pageable pageable) {
		return ResponseWrapper.ok(visitService.findAllForPatient(securityService.currentPatientId(), type, pageable));
	}

}
