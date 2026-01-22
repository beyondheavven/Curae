package com.vention.medfast.webhook.controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import com.vention.medfast.AbstractControllerIntegrationTest;
import com.vention.medfast.webhook.dto.WebhookProcessingDto;
import com.vention.medfast.webhook.service.WebhookService;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class WebhookControllerTest extends AbstractControllerIntegrationTest {

	@MockitoBean
	private WebhookService webhookService;

	private static final String WEBHOOK_URL = "/webhook";

	@Test
	void shouldProcessEventSuccessfullyWhenSignatureIsValid() throws Exception {
		login("john.smith@mail.com", "pass");

		String payload = "{\"id\":\"evt_test123\", \"object\":\"event\"}";
		String sigHeader = "t=123,v1=valid_signature";

		Event mockEvent = mock(Event.class);

		WebhookProcessingDto processingDto = WebhookProcessingDto.builder()
			.status("SUCCESS")
			.message("Processed successfully")
			.build();

		try (MockedStatic<Webhook> webhookMock = mockStatic(Webhook.class)) {
			webhookMock.when(() -> Webhook.constructEvent(anyString(), anyString(), anyString())).thenReturn(mockEvent);

			when(webhookService.processEvent(any(Event.class))).thenReturn(processingDto);

			mockMvc
				.perform(post(WEBHOOK_URL).content(payload)
					.header("Stripe-Signature", sigHeader)
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.status").value("SUCCESS"))
				.andExpect(jsonPath("$.data.message").value("Processed successfully"));
		}
	}

	@Test
	void shouldReturnBadRequestWhenSignatureIsInvalid() throws Exception {
		login("john.smith@mail.com", "pass");

		String payload = "{}";
		String sigHeader = "invalid_sig";

		try (MockedStatic<Webhook> webhookMock = mockStatic(Webhook.class)) {
			webhookMock.when(() -> Webhook.constructEvent(anyString(), anyString(), anyString()))
				.thenThrow(new SignatureVerificationException("Invalid signature", "sig_header"));

			mockMvc
				.perform(post(WEBHOOK_URL).content(payload)
					.header("Stripe-Signature", sigHeader)
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.statusCode").value(400))
				.andExpect(jsonPath("$.error.message").value("Invalid signature"));
		}
	}

	@Test
	void shouldReturnBadRequestWhenSignatureHeaderIsMissing() throws Exception {
		login("john.smith@mail.com", "pass");

		mockMvc.perform(post(WEBHOOK_URL).content("{}").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.statusCode").value(400))
			.andExpect(jsonPath("$.error.message").value("Missing Stripe-Signature header"));
	}

}
