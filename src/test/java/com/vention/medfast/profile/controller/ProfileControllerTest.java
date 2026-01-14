package com.vention.medfast.profile.controller;

import com.vention.medfast.AbstractControllerIntegrationTest;
import com.vention.medfast.profile.dto.request.ContactsUpdateDto;
import com.vention.medfast.profile.dto.request.ProfileAddressUpdateDto;
import com.vention.medfast.profile.dto.request.ProfileRequestDto;
import com.vention.medfast.user.model.Status;
import com.vention.medfast.user.model.User;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProfileControllerTest extends AbstractControllerIntegrationTest {

	@Test
	void shouldReturnProfileForAuthenticatedUser() throws Exception {
		login("john.smith@mail.com", "pass");

		performRequest(HttpMethod.GET, "/profile", null).andExpect(status().isOk())
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
	void shouldUpdateContactsAndMarkPatientUnverifiedWhenEmailChanged() throws Exception {
		login("john.smith@mail.com", "pass");

		String updatedEmail = "john.updated@mail.com";
		String updatedPhone = "+13055559999";

		ContactsUpdateDto contactsUpdateDto = new ContactsUpdateDto(updatedEmail, updatedPhone);
		ProfileRequestDto requestDto = new ProfileRequestDto(null, null, contactsUpdateDto);

		performRequest(HttpMethod.PUT, "/profile", requestDto).andExpect(status().isNoContent());

		login(updatedEmail, "pass");

		em.flush();
		em.clear();

		User databaseUser = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
			.setParameter("email", updatedEmail)
			.getSingleResult();

		assertThat(databaseUser.getEmail()).isEqualTo(updatedEmail);
		assertThat(databaseUser.getStatus()).isEqualTo(Status.UNVERIFIED);
	}

	@Test
	void shouldReturnValidationErrorWhenAddressInvalid() throws Exception {
		ProfileAddressUpdateDto invalidAddress = new ProfileAddressUpdateDto("Broadway", "New York", "101", "10",
				"Unknownland", "ABC");
		ProfileRequestDto requestDto = new ProfileRequestDto(null, invalidAddress, null);

		performRequest(HttpMethod.PUT, "/profile", requestDto).andExpect(status().isBadRequest())
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

}
