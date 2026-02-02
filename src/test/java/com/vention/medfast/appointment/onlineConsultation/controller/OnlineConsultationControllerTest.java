package com.vention.medfast.appointment.onlineConsultation.controller;

import com.vention.medfast.appointment.controller.OnlineConsultationController;
import com.vention.medfast.appointment.dto.CreateOnlineConsultationRequest;
import com.vention.medfast.appointment.dto.OnlineConsultationDetails;
import com.vention.medfast.appointment.dto.UpdateOnlineConsultationRequest;
import com.vention.medfast.appointment.model.AppointmentStatus;
import com.vention.medfast.appointment.model.AppointmentType;
import com.vention.medfast.appointment.service.OnlineConsultationService;
import com.vention.medfast.wrapper.ResponseWrapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ExtendWith(MockitoExtension.class)
class OnlineConsultationControllerTest {

	@Mock
	private OnlineConsultationService service;

	private OnlineConsultationController controller;

	@BeforeEach
	void setUp() {
		controller = new OnlineConsultationController(service);
	}

	@Test
	void getDetailsReturnsWrappedDetails() {
		OnlineConsultationDetails details = new OnlineConsultationDetails(1L, 2L, 3L, LocalDate.now(), LocalTime.NOON,
				AppointmentStatus.SCHEDULED, AppointmentType.ONLINE, LocalDateTime.now(), "https://meet/link");
		when(service.getDetails(1L)).thenReturn(details);

		ResponseWrapper<OnlineConsultationDetails> response = controller.getDetails(1L);

		assertThat(response.getData()).isEqualTo(details);
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
		verify(service).getDetails(1L);
	}

	@Test
	void createOnlineConsultationDelegatesToService() {
		CreateOnlineConsultationRequest request = new CreateOnlineConsultationRequest(10L, 20L,
				LocalDate.now().plusDays(1), LocalTime.of(10, 0), AppointmentStatus.SCHEDULED, "link");

		controller.create(request);

		verify(service).create(request);
	}

	@Test
	void updateOnlineConsultationDelegatesToService() {
		UpdateOnlineConsultationRequest request = new UpdateOnlineConsultationRequest(5L, 10L, 20L,
				LocalDate.now().plusDays(2), LocalTime.of(11, 30), AppointmentStatus.SCHEDULED_CONFIRMED, "link2");

		controller.update(request);

		verify(service).update(request);
	}

	@Test
	void deleteDelegatesToService() {
		controller.delete(7L);

		verify(service).deleteById(7L);
	}

	@Test
	void createEndpointHasCreatedStatusAnnotation() throws Exception {
		ResponseStatus status = OnlineConsultationController.class
			.getMethod("create", CreateOnlineConsultationRequest.class)
			.getAnnotation(ResponseStatus.class);

		assertThat(status).isNotNull();
		assertThat(status.value()).isEqualTo(HttpStatus.CREATED);
	}

	@Test
	void updateEndpointHasNoContentStatusAnnotation() throws Exception {
		ResponseStatus status = OnlineConsultationController.class
			.getMethod("update", UpdateOnlineConsultationRequest.class)
			.getAnnotation(ResponseStatus.class);

		assertThat(status).isNotNull();
		assertThat(status.value()).isEqualTo(HttpStatus.NO_CONTENT);
	}

	@Test
	void deleteEndpointHasNoContentStatusAnnotation() throws Exception {
		ResponseStatus status = OnlineConsultationController.class.getMethod("delete", Long.class)
			.getAnnotation(ResponseStatus.class);

		assertThat(status).isNotNull();
		assertThat(status.value()).isEqualTo(HttpStatus.NO_CONTENT);
	}

}
