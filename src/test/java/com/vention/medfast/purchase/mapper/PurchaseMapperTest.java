package com.vention.medfast.purchase.mapper;

import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.purchase.dto.PurchaseResponseDto;
import com.vention.medfast.purchase.model.PaymentStatus;
import com.vention.medfast.purchase.model.Purchase;
import com.vention.medfast.service.model.ProvidedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PurchaseMapperTest {

	private PurchaseMapper mapper;

	@BeforeEach
	void setUp() {
		mapper = Mappers.getMapper(PurchaseMapper.class);
	}

	@Test
	void shouldMapPurchaseToDtoCorrectly() {
		Long patientId = 15L;
		Long serviceId = 42L;

		Patient patient = new Patient();
		patient.setId(patientId);

		ProvidedService service = new ProvidedService();
		service.setId(serviceId);

		Purchase purchase = new Purchase();
		purchase.setId(1L);
		purchase.setPatient(patient);
		purchase.setProvidedService(service);
		purchase.setStripePaymentIntentId("pi_test_123");
		purchase.setAmount(BigDecimal.valueOf(250.00));
		purchase.setCurrency("USD");
		purchase.setStatus(PaymentStatus.SUCCESS);
		purchase.setCreatedAt(LocalDateTime.now());
		purchase.setUpdatedAt(LocalDateTime.now());

		PurchaseResponseDto dto = mapper.toDto(purchase);

		assertThat(dto).isNotNull();
		assertThat(dto.id()).isEqualTo(purchase.getId());
		assertThat(dto.patientId()).isEqualTo(patientId);
		assertThat(dto.serviceId()).isEqualTo(serviceId);
		assertThat(dto.stripePaymentIntentId()).isEqualTo(purchase.getStripePaymentIntentId());
		assertThat(dto.amount()).isEqualByComparingTo(purchase.getAmount());
		assertThat(dto.status()).isEqualTo(purchase.getStatus().toString());
		assertThat(dto.createdAt()).isEqualTo(purchase.getCreatedAt());
	}

	@Test
	void shouldReturnNullWhenPurchaseIsNull() {
		assertThat(mapper.toDto(null)).isNull();
	}

}
