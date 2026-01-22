package com.vention.medfast.payment.service;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.vention.medfast.config.StripeProperties;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.patient.service.PatientService;
import com.vention.medfast.payment.dto.PaymentRequestDto;
import com.vention.medfast.service.model.ProvidedService;
import com.vention.medfast.service.service.ProvidedServiceService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

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
class PaymentServiceImplTest {

    @Mock
    private ProvidedServiceService providedServiceService;

    @Mock
    private PatientService patientService;

    @Mock
    private StripeProperties stripeProperties;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    void shouldCreateCheckoutSessionWhenInputIsValid() throws StripeException {
        Long serviceId = 1L;
        Long patientId = 100L;
        String currency = "usd";
        String expectedUrl = "https://checkout.stripe.com/pay/cs_test_abc123";
        String successUrl = "http://localhost:8080/success";
        String cancelUrl = "http://localhost:8080/cancel";

        PaymentRequestDto requestDto = new PaymentRequestDto(serviceId, patientId, currency);

        ProvidedService service = new ProvidedService();
        service.setId(serviceId);
        service.setName("Doctor Consultation");
        service.setDescription("General checkup");
        service.setPrice(BigDecimal.valueOf(50.00));

        Patient patient = new Patient();
        patient.setId(patientId);

        when(providedServiceService.getProvidedServiceById(serviceId)).thenReturn(Optional.of(service));
        when(pa.findById(patientId)).thenReturn(Optional.of(patient));

        when(stripeProperties.getPaymentSuccessUrl()).thenReturn(successUrl);
        when(stripeProperties.getPaymentCancelUrl()).thenReturn(cancelUrl);

        try (MockedStatic<Session> sessionMockedStatic = mockStatic(Session.class)) {
            Session mockSession = mock(Session.class);
            when(mockSession.getUrl()).thenReturn(expectedUrl);

            sessionMockedStatic.when(() -> Session.create(any(SessionCreateParams.class)))
                .thenReturn(mockSession);

            String resultUrl = paymentService.createCheckoutSession(requestDto);

            assertEquals(expectedUrl, resultUrl);

            verify(providedServiceRepository).findById(serviceId);
            verify(patientRepository).findById(patientId);

            verify(stripeProperties).getPaymentSuccessUrl();
            verify(stripeProperties).getPaymentCancelUrl();

            sessionMockedStatic.verify(() -> Session.create(any(SessionCreateParams.class)));
        }
    }

    @Test
    void shouldThrowExceptionWhenServiceNotFound() {
        Long serviceId = 1L;
        Long patientId = 100L;
        PaymentRequestDto requestDto = new PaymentRequestDto(serviceId, patientId, "usd");

        when(providedServiceRepository.findById(serviceId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
            () -> paymentService.createCheckoutSession(requestDto));

        assertEquals("Service not found", exception.getMessage());

        verify(providedServiceRepository).findById(serviceId);
        verifyNoInteractions(patientRepository);
        verifyNoInteractions(stripeProperties);
    }

    @Test
    void shouldThrowExceptionWhenPatientNotFound() {
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
        verifyNoInteractions(stripeProperties);
    }
}
