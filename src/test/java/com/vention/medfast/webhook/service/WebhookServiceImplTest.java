package com.vention.medfast.webhook.service;

import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.model.Charge;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.patient.repository.PatientRepository;
import com.vention.medfast.purchase.model.PaymentStatus;
import com.vention.medfast.purchase.model.Purchase;
import com.vention.medfast.purchase.repository.PurchaseRepository;
import com.vention.medfast.service.model.ProvidedService;
import com.vention.medfast.service.repository.ProvidedServiceRepository;
import com.vention.medfast.webhook.dto.WebhookProcessingDto;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebhookServiceImplTest {

	@Mock
	private PurchaseRepository purchaseRepository;

	@Mock
	private PatientRepository patientRepository;

	@Mock
	private ProvidedServiceRepository providedServiceRepository;

	@InjectMocks
	private WebhookServiceImpl webhookService;

	private Event mockStripeEvent(String type, StripeObject stripeObject) {
		Event event = mock(Event.class);
		EventDataObjectDeserializer deserializer = mock(EventDataObjectDeserializer.class);

		lenient().when(event.getType()).thenReturn(type);
		lenient().when(event.getId()).thenReturn("evt_123");
		when(event.getDataObjectDeserializer()).thenReturn(deserializer);
		when(deserializer.getObject()).thenReturn(Optional.ofNullable(stripeObject));

		return event;
	}

	@Test
	void shouldProcessPaymentSucceededAndSavePurchase() {
		String intentId = "pi_123";
		Long amount = 5000L;
		Long patientId = 1L;
		Long serviceId = 2L;

		PaymentIntent paymentIntent = mock(PaymentIntent.class);
		when(paymentIntent.getId()).thenReturn(intentId);
		when(paymentIntent.getAmount()).thenReturn(amount);
		when(paymentIntent.getCurrency()).thenReturn("usd");
		when(paymentIntent.getMetadata())
			.thenReturn(Map.of("patientId", String.valueOf(patientId), "serviceId", String.valueOf(serviceId)));

		Event event = mockStripeEvent("payment_intent.succeeded", paymentIntent);

		when(purchaseRepository.findByStripePaymentIntentId(intentId)).thenReturn(Optional.empty());
		when(patientRepository.findById(patientId)).thenReturn(Optional.of(new Patient()));
		when(providedServiceRepository.findById(serviceId)).thenReturn(Optional.of(new ProvidedService()));

		WebhookProcessingDto result = webhookService.processEvent(event);

		assertEquals("SUCCESS", result.status());
		verify(purchaseRepository).save(any(Purchase.class));
	}

	@Test
	void shouldHandleUnsafeDeserializationCorrectly() throws EventDataObjectDeserializationException {
		PaymentIntent paymentIntent = mock(PaymentIntent.class);
		when(paymentIntent.getId()).thenReturn("pi_unsafe");
		when(paymentIntent.getMetadata()).thenReturn(Map.of("patientId", "1", "serviceId", "2"));
		when(paymentIntent.getAmount()).thenReturn(100L);

		Event event = mock(Event.class);
		EventDataObjectDeserializer deserializer = mock(EventDataObjectDeserializer.class);
		when(event.getDataObjectDeserializer()).thenReturn(deserializer);
		when(event.getId()).thenReturn("evt_unsafe");
		when(event.getType()).thenReturn("payment_intent.succeeded");

		when(deserializer.getObject()).thenReturn(Optional.empty());
		when(deserializer.deserializeUnsafe()).thenReturn(paymentIntent);

		when(purchaseRepository.findByStripePaymentIntentId("pi_unsafe")).thenReturn(Optional.empty());
		when(patientRepository.findById(1L)).thenReturn(Optional.of(new Patient()));
		when(providedServiceRepository.findById(2L)).thenReturn(Optional.of(new ProvidedService()));

		WebhookProcessingDto result = webhookService.processEvent(event);

		assertEquals("SUCCESS", result.status());
		verify(deserializer).deserializeUnsafe();
		verify(purchaseRepository).save(any(Purchase.class));
	}

	@Test
	void shouldReturnErrorIfDeserializationFailsCompletely() throws EventDataObjectDeserializationException {
		Event event = mock(Event.class);
		EventDataObjectDeserializer deserializer = mock(EventDataObjectDeserializer.class);
		when(event.getDataObjectDeserializer()).thenReturn(deserializer);

		when(deserializer.getObject()).thenReturn(Optional.empty());
		when(deserializer.deserializeUnsafe()).thenThrow(new EventDataObjectDeserializationException("Fail", "json"));

		assertThrows(RuntimeException.class, () -> webhookService.processEvent(event));
	}

	@Test
	void shouldReturnErrorIfObjectIsNull() {
		Event event = mock(Event.class);
		EventDataObjectDeserializer deserializer = mock(EventDataObjectDeserializer.class);
		when(event.getDataObjectDeserializer()).thenReturn(deserializer);

		when(deserializer.getObject()).thenReturn(Optional.empty());
		try {
			when(deserializer.deserializeUnsafe()).thenReturn(null);
		}
		catch (EventDataObjectDeserializationException e) {
			throw new RuntimeException(e);
		}

		WebhookProcessingDto result = webhookService.processEvent(event);
		assertEquals("ERROR", result.status());
	}

	@Test
	void shouldSkipDuplicatePurchase() {
		String intentId = "pi_duplicate";
		PaymentIntent paymentIntent = mock(PaymentIntent.class);
		when(paymentIntent.getId()).thenReturn(intentId);

		Event event = mockStripeEvent("payment_intent.succeeded", paymentIntent);

		when(purchaseRepository.findByStripePaymentIntentId(intentId)).thenReturn(Optional.of(new Purchase()));

		webhookService.processEvent(event);

		verify(purchaseRepository, never()).save(any(Purchase.class));
	}

	@Test
	void shouldSkipIfMetadataIsMissing() {
		PaymentIntent paymentIntent = mock(PaymentIntent.class);
		when(paymentIntent.getId()).thenReturn("pi_no_meta");
		when(paymentIntent.getMetadata()).thenReturn(null);

		Event event = mockStripeEvent("payment_intent.succeeded", paymentIntent);

		when(purchaseRepository.findByStripePaymentIntentId(anyString())).thenReturn(Optional.empty());

		webhookService.processEvent(event);

		verify(purchaseRepository, never()).save(any(Purchase.class));
	}

	@Test
	void shouldThrowEntityNotFoundIfPatientOrServiceMissing() {
		PaymentIntent paymentIntent = mock(PaymentIntent.class);
		when(paymentIntent.getId()).thenReturn("pi_missing_entity");
		when(paymentIntent.getMetadata()).thenReturn(Map.of("patientId", "999", "serviceId", "2"));

		Event event = mockStripeEvent("payment_intent.succeeded", paymentIntent);

		when(purchaseRepository.findByStripePaymentIntentId(anyString())).thenReturn(Optional.empty());
		when(patientRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(EntityNotFoundException.class, () -> webhookService.processEvent(event));
	}

	@Test
	void shouldProcessPaymentFailed() {
		String intentId = "pi_failed";
		PaymentIntent paymentIntent = mock(PaymentIntent.class);
		when(paymentIntent.getId()).thenReturn(intentId);
		when(paymentIntent.getAmount()).thenReturn(5000L);
		when(paymentIntent.getCurrency()).thenReturn("usd");
		when(paymentIntent.getMetadata()).thenReturn(Map.of("patientId", "1", "serviceId", "2"));

		Event event = mockStripeEvent("payment_intent.payment_failed", paymentIntent);

		when(purchaseRepository.findByStripePaymentIntentId(intentId)).thenReturn(Optional.empty());
		when(patientRepository.findById(1L)).thenReturn(Optional.of(new Patient()));
		when(providedServiceRepository.findById(2L)).thenReturn(Optional.of(new ProvidedService()));

		WebhookProcessingDto result = webhookService.processEvent(event);

		assertEquals("FAILED", result.status());
		verify(purchaseRepository).save(any(Purchase.class));
	}

	@Test
	void shouldHandleRefundCorrectly() {
		String intentId = "pi_refund";
		Charge charge = mock(Charge.class);
		when(charge.getPaymentIntent()).thenReturn(intentId);
		when(charge.getAmountRefunded()).thenReturn(1000L);
		when(charge.getCurrency()).thenReturn("usd");

		Event event = mockStripeEvent("charge.refunded", charge);

		Purchase existingPurchase = new Purchase();
		existingPurchase.setStatus(PaymentStatus.SUCCESS);

		when(purchaseRepository.findByStripePaymentIntentId(intentId)).thenReturn(Optional.of(existingPurchase));

		WebhookProcessingDto result = webhookService.processEvent(event);

		assertEquals("REFUNDED", result.status());
		assertEquals(PaymentStatus.REFUNDED, existingPurchase.getStatus());
		verify(purchaseRepository).save(existingPurchase);
	}

	@Test
	void shouldIgnoreUnknownEvents() {
		Event event = mockStripeEvent("customer.created", mock(StripeObject.class));

		WebhookProcessingDto result = webhookService.processEvent(event);

		assertEquals("IGNORED", result.status());
		verify(purchaseRepository, never()).save(any());
	}

}
