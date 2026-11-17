package com.vention.medfast.payment.purchase.controller;

import com.vention.medfast.payment.purchase.dto.PurchaseReportDto;
import com.vention.medfast.payment.purchase.service.PurchaseService;
import com.vention.medfast.wrapper.ResponseWrapper;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/purchase")
@RequiredArgsConstructor
public class PurchaseController {

	private final PurchaseService purchaseService;

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping
	public ResponseWrapper<PurchaseReportDto> getReport(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to, Pageable pageable) {
		return ResponseWrapper.ok(purchaseService.getReport(from, to, pageable));
	}

}
