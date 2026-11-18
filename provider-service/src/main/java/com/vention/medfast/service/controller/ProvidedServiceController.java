package com.vention.medfast.service.controller;

import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.service.dto.ProvidedServiceCreateDto;
import com.vention.medfast.service.dto.ProvidedServiceResponseDto;
import com.vention.medfast.service.service.ProvidedServiceService;
import com.vention.medfast.wrapper.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(ApiPaths.SERVICE_ENDPOINT)
@RestController
@RequiredArgsConstructor
@Tag(name = "Services", description = "Endpoints for managing services provided by doctors")
public class ProvidedServiceController {

	private final ProvidedServiceService providedServiceService;

	@Operation(summary = "Get all services")
	@ApiResponses({ @ApiResponse(responseCode = "200"),
			@ApiResponse(responseCode = "204", description = "No services found") })
	@GetMapping
	public ResponseEntity<List<ProvidedServiceResponseDto>> getAll() {
		List<ProvidedServiceResponseDto> services = providedServiceService.getAll();
		if (services.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(services);
	}

	@Operation(summary = "Get service by ID")
	@GetMapping("/{id}")
	public ResponseWrapper<ProvidedServiceResponseDto> getById(@PathVariable("id") Long id) {
		return ResponseWrapper.ok(providedServiceService.getById(id));
	}

	@Operation(summary = "Create service")
	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseWrapper<Long> create(@Valid @RequestBody ProvidedServiceCreateDto dto) {
		return ResponseWrapper.ok(providedServiceService.create(dto));
	}

	@Operation(summary = "Delete a service")
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("id") Long id) {
		providedServiceService.deleteById(id);
	}

	@Operation(summary = "Update an existing service")
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseWrapper<Long> update(@PathVariable("id") Long id, @Valid @RequestBody ProvidedServiceCreateDto dto) {
		return ResponseWrapper.ok(providedServiceService.update(id, dto));
	}

	@Operation(summary = "Search services", description = "Search services by name with pagination")
	@GetMapping(ApiPaths.SEARCH_ENDPOINT)
	public ResponseWrapper<Page<ProvidedServiceResponseDto>> search(
			@Parameter(description = "Search query text (searches in service name)") @RequestParam(
					required = false) String query,
			@ParameterObject @PageableDefault(size = 10, sort = "name",
					direction = Sort.Direction.ASC) Pageable pageable) {
		return ResponseWrapper.ok(providedServiceService.search(query, pageable));
	}

}