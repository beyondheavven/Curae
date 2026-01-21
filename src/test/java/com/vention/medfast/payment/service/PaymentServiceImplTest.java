package com.vention.medfast.payment.service;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.patient.repository.PatientRepository;
import com.vention.medfast.payment.dto.PaymentRequestDto;
import com.vention.medfast.service.model.ProvidedService;
import com.vention.medfast.service.repository.ProvidedServiceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceImplTest {

	@Mock
	private ProvidedServiceRepository providedServiceRepository;

	@Mock
	private PatientRepository patientRepository;

	@InjectMocks
	private PaymentServiceImpl paymentService;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(paymentService, "PAYMENT_SUCCESS_URL", "http://localhost:8080/success");
		ReflectionTestUtils.setField(paymentService, "PAYMENT_CANCEL_URL", "http://localhost:8080/cancel");
	}

	@Test
	public void shouldCreateCheckoutSessionWhenInputIsValid() throws StripeException {
		Long serviceId = 1L;
		Long patientId = 100L;
		String currency = "usd";
		String expectedUrl = "https://checkout.stripe.com/pay/cs_test_abc123";

		PaymentRequestDto requestDto = new PaymentRequestDto(serviceId, patientId, currency);

		ProvidedService service = new ProvidedService();
		service.setId(serviceId);
		service.setName("Doctor Consultation");
		service.setDescription("General checkup");
		service.setPrice(BigDecimal.valueOf(50.00));

		Patient patient = new Patient();
		patient.setId(patientId);

		when(providedServiceRepository.findById(serviceId)).thenReturn(Optional.of(service));
		when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));

		try (MockedStatic<Session> sessionMockedStatic = mockStatic(Session.class)) {
			Session mockSession = mock(Session.class);
			when(mockSession.getUrl()).thenReturn(expectedUrl);

			sessionMockedStatic.when(() -> Session.create(any(SessionCreateParams.class))).thenReturn(mockSession);

			String resultUrl = paymentService.createCheckoutSession(requestDto);

			assertEquals(expectedUrl, resultUrl);

			verify(providedServiceRepository).findById(serviceId);
			verify(patientRepository).findById(patientId);
			sessionMockedStatic.verify(() -> Session.create(any(SessionCreateParams.class)));
		}
	}

	@Test
	public void shouldThrowExceptionWhenServiceNotFound() {
		Long serviceId = 1L;
		Long patientId = 100L;
		PaymentRequestDto requestDto = new PaymentRequestDto(serviceId, patientId, "usd");

		when(providedServiceRepository.findById(serviceId)).thenReturn(Optional.empty());

		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
				() -> paymentService.createCheckoutSession(requestDto));

		assertEquals("Service not found", exception.getMessage());

		verify(providedServiceRepository).findById(serviceId);
		verifyNoInteractions(patientRepository);
	}

	@Test
	public void shouldThrowExceptionWhenPatientNotFound() {
		Long serviceId = 1L;
		Long patientId = 100L;
		PaymentRequestDto requestDto = new PaymentRequestDto(serviceId, patientId, "usd");

		ProvidedService service = new ProvidedService();
		service.setId(serviceId);

		when(providedServiceRepository.findById(serviceId)).thenReturn(Optional.of(service));
		when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
				() -> paymentService.createCheckoutSession(requestDto));

		assertEquals("Patient not found", exception.getMessage());

		verify(providedServiceRepository).findById(serviceId);
		verify(patientRepository).findById(patientId);
	}

}
