package com.vention.medfast.visit.controller;

import com.jayway.jsonpath.JsonPath;
import com.vention.medfast.AbstractControllerIntegrationTest;
import com.vention.medfast.appointment.model.Appointment;
import com.vention.medfast.appointment.model.AppointmentType;
import com.vention.medfast.appointment.model.Status;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.visit.dto.VisitCreateDto;
import com.vention.medfast.visit.model.Visit;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class VisitControllerTest extends AbstractControllerIntegrationTest {

	@Test
	void shouldCreateVisit_whenInputIsValid() throws Exception {
        Appointment appointment = new Appointment();
        appointment.setDate(LocalDate.now().plusDays(1));
        appointment.setTime(LocalTime.of(10, 30));
        appointment.setStatus(Status.SCHEDULED_CONFIRMED);
        appointment.setType(AppointmentType.VISIT);
        appointment.setDoctor(em.find(Doctor.class, 1L));
        appointment.setPatient(em.find(Patient.class, 1L));
        em.persist(appointment);
        em.flush();
        VisitCreateDto dto = new VisitCreateDto(
            appointment.getId(),
            "Diagnosis",
            "Symptoms",
            "Medical report"
        );
        String response = performRequest(HttpMethod.POST, "/visits", dto)
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data").isNumber())
            .andReturn()
            .getResponse()
            .getContentAsString();
        Long createdId = ((Number) JsonPath.read(response, "$.data")).longValue();
        Visit visit = em.find(Visit.class, createdId);
        assertThat(visit).isNotNull();
        assertThat(visit.getAppointment().getId()).isEqualTo(appointment.getId());
        assertThat(visit.getDiagnosis()).isEqualTo("Diagnosis");
        assertThat(visit.getSymptoms()).isEqualTo("Symptoms");
        assertThat(visit.getMedicalReport()).isEqualTo("Medical report");
        assertThat(visit.getAppointment().getStatus()).isEqualTo(Status.COMPLETED);
	}

	@Test
	void shouldReturn400_whenMultipleFieldsAreInvalid() throws Exception {
        VisitCreateDto invalidDto = new VisitCreateDto(
            null,
            "",
            "",
            ""
        );

		performRequest(HttpMethod.POST, "/visits", invalidDto).andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error.errorCode").value("VALIDATION_ERROR"))
			.andExpect(jsonPath("$.error.message").exists());
	}

}
