package com.vention.medfast.specialization.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vention.medfast.auth.jwt.JwtAuthenticationFilter;
import com.vention.medfast.auth.jwt.service.JwtService;
import com.vention.medfast.auth.jwt.service.JwtServiceImpl;
import com.vention.medfast.specialization.dto.SpecializationCreateDto;
import com.vention.medfast.specialization.dto.SpecializationResponseDto;
import com.vention.medfast.specialization.mapper.SpecializationMapper;
import com.vention.medfast.specialization.model.Specialization;
import com.vention.medfast.specialization.service.SpecializationService;
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

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SpecializationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class SpecializationControllerTest {

	@Autowired
	private MockMvc mockMvc;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@MockitoBean
	private SpecializationService specializationService;

	@MockitoBean
	private SpecializationMapper specializationMapper;

	@MockitoBean
	private JwtServiceImpl jwtService;

	@MockitoBean
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@MockitoBean
	private AuthenticationProvider authenticationProvider;

	@Test
	void shouldReturnResponseWhenCreateIsValid() throws Exception {
		SpecializationCreateDto requestDto = SpecializationCreateDto.builder()
			.name("Cardiologist")
			.description("Test description")
			.build();

		Specialization entity = new Specialization();
		entity.setName(requestDto.name());
		entity.setDescription(requestDto.description());
		entity.setId(10L);

		SpecializationResponseDto responseDto = SpecializationResponseDto.builder()
			.id(10L)
			.name("Cardiologist")
			.description("Test description")
			.build();

		given(specializationMapper.toEntity(any(SpecializationCreateDto.class))).willReturn(entity);
		given(specializationService.create(any(SpecializationCreateDto.class))).willReturn(responseDto);
		given(specializationMapper.toDto(any(Specialization.class))).willReturn(responseDto);

		mockMvc
			.perform(post("/api/specializations").contextPath("/api")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id", is(10)))
			.andExpect(jsonPath("$.name", is("Cardiologist")))
			.andExpect(jsonPath("$.description", is("Test description")));

		verify(specializationService, times(1)).create(any(SpecializationCreateDto.class));
	}

	@Test
	void shouldReturnResponseWhenGetByIdExists() throws Exception {
		SpecializationResponseDto responseDto = SpecializationResponseDto.builder()
			.id(10L)
			.name("Cardiologist")
			.description("Test description")
			.build();

		given(specializationService.getById(any(Long.class))).willReturn(responseDto);
		given(specializationMapper.toDto(any(Specialization.class))).willReturn(responseDto);

		ResultActions result = mockMvc
			.perform(get("/api/specializations/{id}", 10L).contextPath("/api").contentType(MediaType.APPLICATION_JSON));

		result.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id", is(10)))
			.andExpect(jsonPath("$.name", is("Cardiologist")))
			.andExpect(jsonPath("$.description", is("Test description")));

		verify(specializationService, times(1)).getById(any(Long.class));
	}

	@Test
	void shouldReturnUpdatedResponseWhenUpdateValid() throws Exception {
		SpecializationCreateDto requestDto = SpecializationCreateDto.builder()
			.name("Cardiologist2")
			.description("Test description2")
			.build();

		SpecializationResponseDto responseDto = SpecializationResponseDto.builder()
			.id(10L)
			.name("Cardiologist2")
			.description("Test description2")
			.build();

		given(specializationService.update(any(Long.class), any(SpecializationCreateDto.class)))
			.willReturn(responseDto);
		given(specializationMapper.toDto(any(Specialization.class))).willReturn(responseDto);

		ResultActions result = mockMvc.perform(put("/api/specializations/{id}", 10L).contextPath("/api")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(requestDto)));

		result.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id", is(10)))
			.andExpect(jsonPath("$.name", is("Cardiologist2")))
			.andExpect(jsonPath("$.description", is("Test description2")));

		verify(specializationService, times(1)).update(any(Long.class), any(SpecializationCreateDto.class));
	}

	@Test
	void shouldReturn404WhenGetResourceByIdNotFound() throws Exception {
		given(specializationService.getById(anyLong()))
			.willThrow(new EntityNotFoundException("Specialization not found"));

		ResultActions result = mockMvc
			.perform(get("/api/specializations/{id}", 10L).contextPath("/api").contentType(MediaType.APPLICATION_JSON));

		result.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message", is("Specialization not found")));

		verify(specializationService, times(1)).getById(any(Long.class));
	}

	@Test
	void shouldReturn404WhenDeleteResourceByIdNotFound() throws Exception {
		doThrow(new EntityNotFoundException("Specialization not found")).when(specializationService)
			.deleteById(anyLong());

		ResultActions result = mockMvc.perform(
				delete("/api/specializations/{id}", 10L).contextPath("/api").contentType(MediaType.APPLICATION_JSON));

		result.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message", is("Specialization not found")));

		verify(specializationService, times(1)).deleteById(any(Long.class));
	}

	@Test
	void shouldReturn404WhenUpdatedResourceNotFound() throws Exception {
		SpecializationCreateDto requestDto = SpecializationCreateDto.builder()
			.name("Cardiologist2")
			.description("Test description2")
			.build();

		doThrow(new EntityNotFoundException("Specialization not found")).when(specializationService)
			.update(anyLong(), any(SpecializationCreateDto.class));

		ResultActions result = mockMvc.perform(put("/api/specializations/{id}", 10L).contextPath("/api")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(requestDto)));

		result.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message", is("Specialization not found")));

		verify(specializationService, times(1)).update(any(Long.class), any(SpecializationCreateDto.class));
	}

	@Test
	void shouldReturn204WhenDeleteSuccessful() throws Exception {
		doNothing().when(specializationService).deleteById(1L);

		ResultActions result = mockMvc.perform(delete("/api/specializations/{id}", 1L).contextPath("/api"));

		verify(specializationService, times(1)).deleteById(1L);
		result.andDo(print()).andExpect(status().isNoContent());
	}

	@Test
	void shouldReturn400WhenCreateSpecializationWithInvalidNameAndDescriptionFields() throws Exception {
		SpecializationCreateDto requestDto = SpecializationCreateDto.builder().name("T").description("D").build();

		mockMvc
			.perform(post("/api/specializations").contextPath("/api")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.fieldErrors.name", is("Name should be between 3 and 50 characters")))
			.andExpect(
					jsonPath("$.fieldErrors.description", is("Description should be between 10 and 255 characters")));

		verify(specializationService, times(0)).create(any(SpecializationCreateDto.class));
	}

	@Test
	void shouldReturn400WhenCreateSpecializationWithNullNameField() throws Exception {
		SpecializationCreateDto requestDto = SpecializationCreateDto.builder()
			.name(null)
			.description("Test description")
			.build();

		mockMvc
			.perform(post("/api/specializations").contextPath("/api")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.fieldErrors.name", is("Name is required")));

		verify(specializationService, times(0)).create(any(SpecializationCreateDto.class));
	}

	@Test
	void shouldReturn400WhenUpdateSpecializationWithBlankNameField() throws Exception {
		SpecializationCreateDto requestDto = SpecializationCreateDto.builder()
			.name("")
			.description("Test description")
			.build();

		mockMvc
			.perform(put("/api/specializations/{id}", 1L).contextPath("/api")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.fieldErrors.name", is("Name should be between 3 and 50 characters")));

		verify(specializationService, times(0)).update(any(Long.class), any(SpecializationCreateDto.class));
	}

	@Test
	void shouldReturnAllSpecializationsWhenSpecializationsExist() throws Exception {
		List<SpecializationResponseDto> specializations = List.of(
				SpecializationResponseDto.builder().id(1L).name("Cardiologist").description("Heart specialist").build(),
				SpecializationResponseDto.builder()
					.id(2L)
					.name("General Practitioner")
					.description("Primary care provider")
					.build());

		given(specializationService.getAll()).willReturn(specializations);

		mockMvc.perform(get("/api/specializations").contextPath("/api").contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(2)))
			.andExpect(jsonPath("$[0].id", is(1)))
			.andExpect(jsonPath("$[0].name", is("Cardiologist")))
			.andExpect(jsonPath("$[0].description", is("Heart specialist")))
			.andExpect(jsonPath("$[1].id", is(2)))
			.andExpect(jsonPath("$[1].name", is("General Practitioner")))
			.andExpect(jsonPath("$[1].description", is("Primary care provider")));

		verify(specializationService, times(1)).getAll();
	}

	@Test
	void shouldReturnEmptyListWhenSpecializationsAreNull() throws Exception {
		given(specializationService.getAll()).willReturn(new ArrayList<>());

		mockMvc.perform(get("/api/specializations").contextPath("/api").contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isNoContent());

		verify(specializationService, times(1)).getAll();
	}

}
