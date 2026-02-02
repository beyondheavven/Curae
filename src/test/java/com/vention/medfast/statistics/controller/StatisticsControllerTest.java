package com.vention.medfast.statistics.controller;

import com.vention.medfast.AbstractControllerIntegrationTest;
import com.vention.medfast.appointment.model.AppointmentStatus;
import com.vention.medfast.appointment.model.AppointmentType;
import com.vention.medfast.appointment.model.Visit;
import com.vention.medfast.clinic.model.Clinic;
import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.labresult.model.LabResult;
import com.vention.medfast.labresult.model.LabResultStatus;
import com.vention.medfast.labtest.model.LabTest;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.service.model.ProvidedService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StatisticsControllerTest extends AbstractControllerIntegrationTest {

	@Test
	void shouldReturnStatisticsWhenInputIsValid() throws Exception {
		Patient patient = em.find(Patient.class, 1L);
		Clinic clinic = em.find(Clinic.class, 1L);
		LabTest labTestRef = em.find(LabTest.class, 1L);
		Doctor doctor = em.find(Doctor.class, 1L);
		ProvidedService service = em.find(ProvidedService.class, 1L);

		Visit visit = new Visit();
		visit.setPatient(patient);
		visit.setDoctor(doctor);
		visit.setClinic(clinic);
		visit.setProvidedService(service);
		visit.setDate(LocalDate.of(2026, 1, 10));
		visit.setTime(LocalTime.NOON);
		visit.setStatus(AppointmentStatus.COMPLETED);
		visit.setType(AppointmentType.VISIT);
		visit.setDiagnosis("Test");
		visit.setSymptoms("Test");
		visit.setMedicalReport("Test");
		em.persist(visit);

		com.vention.medfast.appointment.model.Test testAppointment = new com.vention.medfast.appointment.model.Test();
		testAppointment.setPatient(patient);
		testAppointment.setClinic(clinic);
		testAppointment.setDate(LocalDate.of(2026, 1, 15));
		testAppointment.setTime(LocalTime.NOON);
		testAppointment.setStatus(AppointmentStatus.COMPLETED);
		testAppointment.setType(AppointmentType.TEST);
		em.persist(testAppointment);

		LabResult resultProcessing = new LabResult();
		resultProcessing.setTest(testAppointment);
		resultProcessing.setLabTest(labTestRef);
		resultProcessing.setStatus(LabResultStatus.PROCESSING);
		em.persist(resultProcessing);

		LabResult resultReady = new LabResult();
		resultReady.setTest(testAppointment);
		resultReady.setLabTest(labTestRef);
		resultReady.setStatus(LabResultStatus.READY);
		resultReady.setResultUrl("http://test.com");
		em.persist(resultReady);

		em.flush();

		LocalDate startDate = LocalDate.of(2026, 1, 1);
		LocalDate endDate = LocalDate.of(2026, 1, 24);

		String url = String.format("%s%s?startDate=%s&endDate=%s", ApiPaths.STATISTICS_ENDPOINT,
				ApiPaths.ADMIN_ENDPOINT, startDate, endDate);

		performRequest(HttpMethod.GET, url, null).andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.period.startDate").value(startDate.toString()))
			.andExpect(jsonPath("$.data.period.endDate").value(endDate.toString()))

			.andExpect(jsonPath("$.data.appointments.stats.total").value(Matchers.greaterThanOrEqualTo(2)))
			.andExpect(jsonPath("$.data.appointments.stats.completed").value(Matchers.greaterThanOrEqualTo(2)))
			.andExpect(jsonPath("$.data.visits.stats.total").value(Matchers.greaterThanOrEqualTo(1)))
			.andExpect(jsonPath("$.data.visits.stats.completed").value(Matchers.greaterThanOrEqualTo(1)))
			.andExpect(jsonPath("$.data.labResults.stats.total").value(Matchers.greaterThanOrEqualTo(2)))
			.andExpect(jsonPath("$.data.labResults.results.processing").value(Matchers.greaterThanOrEqualTo(1)))
			.andExpect(jsonPath("$.data.labResults.results.ready").value(Matchers.greaterThanOrEqualTo(1)));
	}

	@Test
	void shouldReturnBadRequestWhenDateRangeIsInvalid() throws Exception {
		LocalDate startDate = LocalDate.of(2026, 1, 12);
		LocalDate endDate = LocalDate.of(2026, 1, 1);

		String url = String.format("%s%s?startDate=%s&endDate=%s", ApiPaths.STATISTICS_ENDPOINT,
				ApiPaths.ADMIN_ENDPOINT, startDate, endDate);

		performRequest(HttpMethod.GET, url, null).andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error.errorCode").value("VALIDATION_ERROR"))
			.andExpect(jsonPath("$.error.message").value("Validation failed"))
			.andExpect(jsonPath("$.error.validationErrors[0].field").value("statisticsPeriodRequestDto"))
			.andExpect(jsonPath("$.error.validationErrors[0].message").value("End date must be before start date"));
	}

	@Test
	void shouldReturnBadRequestWhenParameterIsMissing() throws Exception {
		LocalDate startDate = LocalDate.of(2026, 1, 1);

		String url = String.format("%s%s?startDate=%s", ApiPaths.STATISTICS_ENDPOINT, ApiPaths.ADMIN_ENDPOINT,
				startDate);

		performRequest(HttpMethod.GET, url, null).andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error.errorCode").value("VALIDATION_ERROR"))
			.andExpect(jsonPath("$.error.validationErrors[0].field").value("endDate"))
			.andExpect(jsonPath("$.error.validationErrors[0].message").value("End date is required"));
	}

	@Test
	void shouldReturnBadRequestWhenBothParametersMissing() throws Exception {
		String url = ApiPaths.STATISTICS_ENDPOINT + ApiPaths.ADMIN_ENDPOINT;

		performRequest(HttpMethod.GET, url, null).andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error.errorCode").value("VALIDATION_ERROR"))
			.andExpect(jsonPath("$.error.validationErrors", hasSize(2)))
			.andExpect(jsonPath("$.error.validationErrors[*].field", containsInAnyOrder("startDate", "endDate")));
	}

	@Test
	void shouldReturnBadRequestWhenDateIsInFuture() throws Exception {
		LocalDate futureStartDate = LocalDate.now().plusYears(1);
		LocalDate endDate = futureStartDate.plusDays(5);

		String url = String.format("%s%s?startDate=%s&endDate=%s", ApiPaths.STATISTICS_ENDPOINT,
				ApiPaths.ADMIN_ENDPOINT, futureStartDate, endDate);

		performRequest(HttpMethod.GET, url, null).andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error.validationErrors[0].field").value("startDate"))
			.andExpect(jsonPath("$.error.validationErrors[0].rejectedValue").value(futureStartDate.toString()))
			.andExpect(jsonPath("$.error.validationErrors[0].message").value("Start date cannot be in the future"));
	}

}
