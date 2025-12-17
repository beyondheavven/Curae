package com.vention.medfast.patient.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vention.medfast.address.dto.AddressCreateDto;
import com.vention.medfast.auth.jwt.JwtAuthenticationFilter;
import com.vention.medfast.auth.jwt.service.JwtServiceImpl;
import com.vention.medfast.patient.dto.*;
import com.vention.medfast.patient.mapper.PatientMapper;
import com.vention.medfast.patient.model.GenderType;
import com.vention.medfast.patient.service.PatientService;
import com.vention.medfast.specialization.mapper.SpecializationMapper;
import com.vention.medfast.user.dto.UserCreateDto;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PatientController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PatientControllerTest {

	@Autowired
	private MockMvc mockMvc;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@MockitoBean
	private PatientService patientService;

	@MockitoBean
	private PatientMapper patientMapper;

	@MockitoBean
	private JwtServiceImpl jwtService;

	@MockitoBean
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@MockitoBean
	private AuthenticationProvider authenticationProvider;

	@Test
	void shouldReturnResponseWhenCreateIsValid() throws Exception {
		objectMapper.registerModule(new JavaTimeModule());
		UserCreateDto userDto = new UserCreateDto("John", "Doe", "john.doe@example.com", "password123",
				"http://profile.url");
		AddressCreateDto addressDto = new AddressCreateDto("Baker St", "London", "221B", "UK", "1", "NW1 6XE",
				"+4412345678");

		PatientCreateDto requestDto = new PatientCreateDto(userDto, addressDto, "USA", GenderType.FEMALE,
				LocalDate.of(1990, 1, 1));

		PatientResponseDto responseDto = PatientResponseDto.builder()
			.id(1L)
			.firstName("John")
			.lastName("Doe")
			.profileUrl("http://profile.url")
			.build();

		given(patientService.create(any(PatientCreateDto.class))).willReturn(responseDto);

		mockMvc
			.perform(post("/api/patients").contextPath("/api")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id", is(1)))
			.andExpect(jsonPath("$.firstName", is("John")))
			.andExpect(jsonPath("$.lastName", is("Doe")))
			.andExpect(jsonPath("$.profileUrl", is("http://profile.url")));

		verify(patientService).create(any(PatientCreateDto.class));
	}

	@Test
	void shouldReturnResponseWhenGetByIdExists() throws Exception {
		PatientResponseDto responseDto = PatientResponseDto.builder()
			.id(10L)
			.firstName("Alice")
			.lastName("Smith")
			.profileUrl("http://alice.img")
			.build();

		given(patientService.getPatientById(10L)).willReturn(responseDto);

		mockMvc.perform(get("/api/patients/{id}", 10L).contextPath("/api").contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id", is(10)))
			.andExpect(jsonPath("$.firstName", is("Alice")))
			.andExpect(jsonPath("$.lastName", is("Smith")));

		verify(patientService, times(1)).getPatientById(10L);
	}

	@Test
	void shouldReturn404WhenGetPatientByIdNotFound() throws Exception {
		given(patientService.getPatientById(anyLong())).willThrow(new EntityNotFoundException("Patient not found"));

		ResultActions result = mockMvc
			.perform(get("/api/patients/{id}", 999L).contextPath("/api").contentType(MediaType.APPLICATION_JSON));

		result.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message", is("Patient not found")));

		verify(patientService, times(1)).getPatientById(999L);
	}

	@Test
	void shouldReturn400WhenCreatePatientInvalid() throws Exception {
		objectMapper.registerModule(new JavaTimeModule());
		PatientCreateDto invalidDto = new PatientCreateDto(null, null, "", null, LocalDate.now().plusDays(1));

		mockMvc
			.perform(post("/api/patients").contextPath("/api")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidDto)))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.statusCode", is(400)));

		verify(patientService, times(0)).create(any());
	}

}
