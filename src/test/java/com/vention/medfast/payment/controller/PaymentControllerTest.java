package com.vention.medfast.payment.controller;

import com.stripe.exception.InvalidRequestException;
import com.stripe.exception.StripeException;
import com.vention.medfast.AbstractControllerIntegrationTest;
import com.vention.medfast.payment.dto.PaymentRequestDto;
import com.vention.medfast.payment.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PaymentControllerTest extends AbstractControllerIntegrationTest {

	@MockitoBean
	private PaymentService paymentService;

	@Test
	void shouldReturnPaymentUrlOnSuccessfulCheckout() throws Exception {
		login("john.smith@mail.com", "pass");

		Long patientId = 1L;
		Long serviceId = 2L;
		String currency = "usd";
		String expectedUrl = "https://checkout.stripe.com/pay/cs_test_a1b2c3d4";

		PaymentRequestDto requestDto = new PaymentRequestDto(serviceId, patientId, currency);

		when(paymentService.createCheckoutSession(any(PaymentRequestDto.class))).thenReturn(expectedUrl);

		performRequest(HttpMethod.POST, "/payment/checkout", requestDto).andExpect(status().isOk())
			.andExpect(jsonPath("$.data").value(expectedUrl))
			.andExpect(jsonPath("$.error").doesNotExist());
	}

	@Test
	void shouldReturnBadRequestWhenStripeExceptionOccurs() throws Exception {
		login("john.smith@mail.com", "pass");

		PaymentRequestDto requestDto = new PaymentRequestDto(99L, 1L, "usd");
		String errorMessage = "Invalid API Key or Service unavailable";

		StripeException stripeException = new InvalidRequestException(errorMessage, null, null, null, 400, null);

		when(paymentService.createCheckoutSession(any(PaymentRequestDto.class))).thenThrow(stripeException);

		performRequest(HttpMethod.POST, "/payment/checkout", requestDto).andExpect(status().isBadRequest())

			.andExpect(jsonPath("$.statusCode").value(400))
			.andExpect(jsonPath("$.error.errorCode").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.error.message").value(errorMessage));
	}

	@Test
	void shouldReturnUnauthorizedWhenTokenMissing() throws Exception {
		PaymentRequestDto requestDto = new PaymentRequestDto(1L, 1L, "usd");

		mockMvc
			.perform(post("/payment/checkout").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isForbidden());
	}

}
