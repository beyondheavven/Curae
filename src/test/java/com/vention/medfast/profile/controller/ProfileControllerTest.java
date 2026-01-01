package com.vention.medfast.profile.controller;

import com.jayway.jsonpath.JsonPath;
import com.vention.medfast.AbstractIntegrationTest;
import com.vention.medfast.auth.dto.AuthRequestDto;
import com.vention.medfast.profile.dto.request.ContactsUpdateDto;
import com.vention.medfast.profile.dto.request.ProfileAddressUpdateDto;
import com.vention.medfast.profile.dto.request.ProfileRequestDto;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProfileControllerTest extends AbstractIntegrationTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	private String token;

	@BeforeAll
	void obtainToken() throws Exception {
		token = loginAndGetToken("john.smith@mail.com", "pass");
	}

	@Test
	void shouldReturnProfileForAuthenticatedUser() throws Exception {
		mockMvc.perform(get("/profile").header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())

			.andExpect(jsonPath("$.data.personalDetails.name").value("John"))
			.andExpect(jsonPath("$.data.personalDetails.lastName").value("Smith"))
			.andExpect(jsonPath("$.data.personalDetails.birthDate").value("1985-10-20"))
			.andExpect(jsonPath("$.data.personalDetails.gender").value("MALE"))
			.andExpect(jsonPath("$.data.personalDetails.citizenship").value("American"))
			.andExpect(jsonPath("$.data.personalDetails.photoUrl").value(Matchers.nullValue()))

			.andExpect(jsonPath("$.data.status").value("ACTIVE"))

			.andExpect(jsonPath("$.data.profileAddress.state").value("FL"))
			.andExpect(jsonPath("$.data.profileAddress.city").value("Miami"))
			.andExpect(jsonPath("$.data.profileAddress.street").value("Ocean Drive"))
			.andExpect(jsonPath("$.data.profileAddress.zipCode").value("33101"))
			.andExpect(jsonPath("$.data.profileAddress.houseNumber").value("500"))
			.andExpect(jsonPath("$.data.profileAddress.apartment").value("12"))

			.andExpect(jsonPath("$.data.contactsDto.phone").value("+13055550300"))
			.andExpect(jsonPath("$.data.contactsDto.email").value("john.smith@mail.com"));
	}

	@Test
	@org.springframework.transaction.annotation.Transactional
	void shouldUpdateContactsAndMarkPatientUnverifiedWhenEmailChanged() throws Exception {
		String updatedEmail = "john.updated@mail.com";
		String updatedPhone = "+13055559999";

		ContactsUpdateDto contactsUpdateDto = new ContactsUpdateDto(updatedEmail, updatedPhone);
		ProfileRequestDto requestDto = new ProfileRequestDto(null, null, contactsUpdateDto);

		String json = objectMapper.writeValueAsString(requestDto);

		mockMvc
			.perform(put("/profile").header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
			.andExpect(status().isNoContent());

		String updatedToken = loginAndGetToken(updatedEmail, "pass");

		mockMvc.perform(get("/profile").header("Authorization", "Bearer " + updatedToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.contactsDto.phone").value(updatedPhone))
			.andExpect(jsonPath("$.data.contactsDto.email").value(updatedEmail))
			.andExpect(jsonPath("$.data.status").value("UNVERIFIED"));
	}

	@Test
	void shouldReturnValidationErrorWhenAddressInvalid() throws Exception {
		ProfileAddressUpdateDto invalidAddress = new ProfileAddressUpdateDto("Broadway", "New York", "101", "10",
				"Unknownland", "ABC");
		ProfileRequestDto requestDto = new ProfileRequestDto(null, invalidAddress, null);

		String json = objectMapper.writeValueAsString(requestDto);

		mockMvc
			.perform(put("/profile").header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error.errorCode").value("VALIDATION_ERROR"))
			.andExpect(jsonPath("$.error.message", Matchers.containsString("Unknown country")));
	}

	@Test
	void shouldReturnForbiddenWhenTokenMissing() throws Exception {
		mockMvc.perform(get("/profile")).andExpect(status().isForbidden());
	}

	@Test
	void shouldReturnUnauthorizedWhenTokenInvalid() throws Exception {
		mockMvc.perform(get("/profile").header("Authorization", "Bearer " + "not-a-valid-jwt"))
			.andExpect(status().isUnauthorized());
	}

	private String loginAndGetToken(String email, String password) throws Exception {
		AuthRequestDto loginRequest = new AuthRequestDto(email, password);
		String json = objectMapper.writeValueAsString(loginRequest);

		String response = mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(json))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();

		return JsonPath.read(response, "$.data.accessToken");
	}

}
