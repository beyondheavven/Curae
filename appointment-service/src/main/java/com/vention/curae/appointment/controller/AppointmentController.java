package com.vention.curae.appointment.controller;

import com.vention.curae.appointment.dto.AppointmentCreateDto;
import com.vention.curae.appointment.dto.AppointmentUpdateDto;
import com.vention.curae.appointment.service.AppointmentService;
import com.vention.curae.wrapper.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointments", description = "Endpoints for managing appointments")
public class AppointmentController {

	private final AppointmentService appointmentService;

	@PreAuthorize("hasRole('PATIENT')")
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Create appointment")
	public ResponseWrapper<Long> create(@Valid @RequestBody AppointmentCreateDto dto) {
		return ResponseWrapper.withStatus(HttpStatus.CREATED, appointmentService.create(dto));
	}

	@PreAuthorize("hasRole('PATIENT')")
	@PostMapping("/{id}/confirm")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Confirm appointment")
	public void confirm(@PathVariable Long id) {
		appointmentService.confirm(id);
	}

	@PatchMapping("/{id}/cancel")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Cancel appointment")
	public void cancel(@PathVariable Long id) {
		appointmentService.cancelAppointment(id);
	}

	@PreAuthorize("hasRole('PATIENT')")
	@PutMapping("/{id}")
	@Operation(summary = "Update appointment")
	public ResponseWrapper<Long> update(@PathVariable Long id, @Valid @RequestBody AppointmentUpdateDto dto) {
		appointmentService.update(id, dto);
		return ResponseWrapper.ok(id);
	}

	@GetMapping("/internal/completed")
	@Operation(summary = "Check completed appointment exists for patient and doctor")
	public ResponseWrapper<Boolean> existsCompleted(@RequestParam Long patientId, @RequestParam Long doctorId) {
		return ResponseWrapper.ok(appointmentService.existsByPatientIdDoctorIdAndStatus(patientId, doctorId,
				com.vention.curae.appointment.model.AppointmentStatus.COMPLETED));
	}

}
