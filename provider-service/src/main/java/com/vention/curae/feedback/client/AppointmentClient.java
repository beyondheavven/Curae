package com.vention.curae.feedback.client;

import com.vention.curae.wrapper.ResponseWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AppointmentClient {

	private final RestClient restClient;

	public AppointmentClient(RestClient.Builder builder,
			@Value("${services.appointment.url:http://appointment-service:8082}") String appointmentServiceUrl) {
		this.restClient = builder.baseUrl(appointmentServiceUrl).build();
	}

	public boolean hasCompletedAppointment(Long patientId, Long doctorId) {
		ResponseWrapper<Boolean> result = restClient.get()
			.uri(uriBuilder -> uriBuilder.path("/appointments/internal/completed")
				.queryParam("patientId", patientId)
				.queryParam("doctorId", doctorId)
				.build())
			.retrieve()
			.body(new ParameterizedTypeReference<ResponseWrapper<Boolean>>() {
			});
		return result != null && Boolean.TRUE.equals(result.getData());
	}

}