package com.vention.curae.appointment.controller;

import com.vention.curae.appointment.dto.PatientLabHistoryDTO;
import com.vention.curae.appointment.dto.TestCreateWithLabsDto;
import com.vention.curae.appointment.security.AppointmentSecurityService;
import com.vention.curae.appointment.service.TestService;
import com.vention.curae.wrapper.ResponseWrapper;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
@Tag(name = "Tests", description = "Endpoints for lab appointments")
public class TestController {

	private final TestService testService;

	private final AppointmentSecurityService securityService;

	@PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
	@PostMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Create lab appointment")
	public void create(@Valid @RequestBody TestCreateWithLabsDto dto) {
		testService.createWithLabs(dto);
	}

	@PreAuthorize("hasRole('PATIENT')")
	@GetMapping("/history")
	@Operation(summary = "Get patient lab history")
	public ResponseWrapper<Page<PatientLabHistoryDTO>> getHistory(@ParameterObject Pageable pageable) {
		return ResponseWrapper.ok(testService.getPatientLabHistory(securityService.currentPatientId(), pageable));
	}

}
