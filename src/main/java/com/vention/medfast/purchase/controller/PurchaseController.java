package com.vention.medfast.purchase.controller;

import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.purchase.dto.PurchaseReportDto;
import com.vention.medfast.purchase.service.PurchaseService;
import com.vention.medfast.wrapper.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping(ApiPaths.PURCHASE_ENDPOINT)
@RequiredArgsConstructor
@Tag(name = "Purchases", description = "Endpoints for managing purchases")
public class PurchaseController {

	private final PurchaseService purchaseService;

	@Operation(summary = "Get purchase report",
			description = "Returns a report of purchases between the specified dates.")
	@GetMapping()
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseWrapper<PurchaseReportDto> getPurchaseReport(@RequestParam LocalDateTime from,
			@RequestParam LocalDateTime to, @ParameterObject Pageable pageable) {
		return ResponseWrapper.ok(purchaseService.getReport(from, to, pageable));
	}

}
