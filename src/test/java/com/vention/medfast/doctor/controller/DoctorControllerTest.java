package com.vention.medfast.doctor.controller;

import com.vention.medfast.AbstractControllerIntegrationTest;
import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.doctor.dto.DoctorClinicAssignmentRequestDto;
import com.vention.medfast.doctor.dto.DoctorUpdateDto;
import com.vention.medfast.doctor.util.DataUtils;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DoctorControllerTest extends AbstractControllerIntegrationTest {

	@Test
	void shouldSearchDoctorsByNameWhenMatchExists() throws Exception {
		String url = ApiPaths.DOCTOR_ENDPOINT + ApiPaths.SEARCH_ENDPOINT + "?query=Watson&page=0&size=10";

		performRequest(GET, url, null).andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.totalElements").value(1))
			.andExpect(jsonPath("$.data.totalPages").value(1))
			.andExpect(jsonPath("$.data.first").value(true))
			.andExpect(jsonPath("$.data.size").value(10))
			.andExpect(jsonPath("$.data.content[0].firstName").value("John"))
			.andExpect(jsonPath("$.data.content[0].lastName").value("Watson"))
			.andExpect(jsonPath("$.data.content[0].specialization").value("General Practitioner"));
	}

	@Test
	void shouldSearchDoctorsBySpecializationWhenMatchExists() throws Exception {
		String url = ApiPaths.DOCTOR_ENDPOINT + ApiPaths.SEARCH_ENDPOINT + "?specializationIds=3&page=0&size=10";

		performRequest(GET, url, null).andExpect(status().isOk())
			.andExpect(jsonPath("$.data.totalElements").value(1))
			.andExpect(jsonPath("$.data.content[0].lastName").value("House"));
	}

	@Test
	void shouldAssignClinicsToDoctorWhenInputIsValid() throws Exception {
		Set<Long> clinicIds = Set.of(1L);
		DoctorClinicAssignmentRequestDto requestDto = new DoctorClinicAssignmentRequestDto(clinicIds);

		performRequest(PUT, ApiPaths.DOCTOR_ENDPOINT + "/{id}" + ApiPaths.CLINIC_ENDPOINT, requestDto,
				DataUtils.DR_WATSON_ID)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.clinics").isArray())
			.andExpect(jsonPath("$.data.clinics").value(hasSize(1)))
			.andExpect(jsonPath("$.data.clinics[0].id").value(1));

		performRequest(GET, ApiPaths.DOCTOR_ENDPOINT + "/{id}", null, DataUtils.DR_WATSON_ID).andExpect(status().isOk())
			.andExpect(jsonPath("$.data.clinics", hasSize(1)));
	}

	@Test
	void shouldReturnBadRequestWhenAssigningNonExistentClinics() throws Exception {
		Set<Long> nonExistentIds = Set.of(99999L);
		DoctorClinicAssignmentRequestDto requestDto = new DoctorClinicAssignmentRequestDto(nonExistentIds);

		performRequest(PUT, ApiPaths.DOCTOR_ENDPOINT + "/{id}" + ApiPaths.CLINIC_ENDPOINT, requestDto,
				DataUtils.DR_WATSON_ID)
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@Test
	void shouldReturnNotFoundWhenAssigningClinicsToNonExistentDoctor() throws Exception {
		Set<Long> clinicIds = Set.of(1L);
		DoctorClinicAssignmentRequestDto requestDto = new DoctorClinicAssignmentRequestDto(clinicIds);

		performRequest(PUT, ApiPaths.DOCTOR_ENDPOINT + "/{id}" + ApiPaths.CLINIC_ENDPOINT, requestDto, 9999L)
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.error.message").value("Doctor not found"));
	}

	@Test
	void shouldReturnDoctorByIdWhenInputIsValid() throws Exception {
		performRequest(GET, ApiPaths.DOCTOR_ENDPOINT + "/{id}", null, DataUtils.DR_HOUSE_ID).andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.id").value(DataUtils.DR_HOUSE_ID))
			.andExpect(jsonPath("$.data.firstName").value("Gregory"))
			.andExpect(jsonPath("$.data.lastName").value("House"))
			.andExpect(jsonPath("$.data.email").value(DataUtils.HOUSE_EMAIL))
			.andExpect(jsonPath("$.data.bio").value("World-renowned diagnostician with unconventional methods."))
			.andExpect(jsonPath("$.data.licenseNumber").value("1238495"))
			.andExpect(jsonPath("$.data.phoneNumber").value("+12112550100"))
			.andExpect(jsonPath("$.data.specializations").isArray())
			.andExpect(jsonPath("$.data.specializations", hasSize(2)))
			.andExpect(jsonPath("$.data.specializations[*].id").value(hasItems(1, 3)))
			.andExpect(
					jsonPath("$.data.specializations[*].name").value(hasItems("Diagnostician", "General Practitioner")))
			.andExpect(jsonPath("$.data.clinics").isArray())
			.andExpect(jsonPath("$.data.clinics").value(hasSize(1)))
			.andExpect(jsonPath("$.data.clinics[0].id").value(1))
			.andExpect(jsonPath("$.data.clinics[0].name").value("Central City Hospital"));
	}

	@Test
	void shouldUpdateDoctorWhenInputIsValid() throws Exception {
		DoctorUpdateDto updateDto = DataUtils.getDoctorUpdateDtoIntegration();

		performRequest(PUT, ApiPaths.DOCTOR_ENDPOINT + "/{id}", updateDto, DataUtils.DR_HOUSE_ID)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.firstName").value("GregoryUPDATED"))
			.andExpect(jsonPath("$.data.lastName").value("HouseUPDATED"))
			.andExpect(jsonPath("$.data.phoneNumber").value("+19998887777"))
			.andExpect(jsonPath("$.data.licenseNumber").value("LIC-99999"))
			.andExpect(jsonPath("$.data.specializations").value(hasSize(1)))
			.andExpect(jsonPath("$.data.specializations[0].id").value(2))
			.andExpect(jsonPath("$.data.specializations[0].name").value("Cardiologist"));

		performRequest(GET, ApiPaths.DOCTOR_ENDPOINT + "/{id}", null, DataUtils.DR_HOUSE_ID).andExpect(status().isOk())
			.andExpect(jsonPath("$.data.firstName").value("GregoryUPDATED"))
			.andExpect(jsonPath("$.data.lastName").value("HouseUPDATED"));
	}

	@Test
	void shouldDeleteDoctorWhenIdIsValid() throws Exception {
		performRequest(DELETE, ApiPaths.DOCTOR_ENDPOINT + "/{id}", null, DataUtils.DR_WATSON_ID)
			.andExpect(status().isNoContent());

		performRequest(GET, ApiPaths.DOCTOR_ENDPOINT + "/{id}", null, DataUtils.DR_WATSON_ID)
			.andExpect(status().isNotFound());
	}

}
