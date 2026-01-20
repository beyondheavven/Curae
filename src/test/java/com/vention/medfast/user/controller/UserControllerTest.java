package com.vention.medfast.user.controller;

import com.vention.medfast.AbstractControllerIntegrationTest;
import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.user.dto.UserUpdateRequestDto;
import com.vention.medfast.user.model.Status;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import java.time.LocalDate;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest extends AbstractControllerIntegrationTest {

	@Test
	void shouldReturnUserByIdWhenInputIsValid() throws Exception {
		performRequest(HttpMethod.GET, ApiPaths.USER_ENDPOINT + "/{id}", null, 2L).andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.id").value(2L))
			.andExpect(jsonPath("$.data.email").value("house@med.com"))
			.andExpect(jsonPath("$.data.firstName").value("Gregory"))
			.andExpect(jsonPath("$.data.lastName").value("House"))
			.andExpect(jsonPath("$.data.profileUrl").value("https://img.url/house.jpg"))
			.andExpect(jsonPath("$.data.birthDate").value("2000-01-01"))
			.andExpect(jsonPath("$.data.role").value("DOCTOR"))
			.andExpect(jsonPath("$.data.status").value("UNVERIFIED"));
	}

	@Test
	void shouldReturnNotFoundWhenGetUserByNonExistentId() throws Exception {
		performRequest(HttpMethod.GET, ApiPaths.USER_ENDPOINT + "/{id}", null, 99999L).andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.error.errorCode").value("NOT_FOUND"))
			.andExpect(jsonPath("$.error.message").value("User not found"))
			.andExpect(jsonPath("$.status").value("404 NOT_FOUND"))
			.andExpect(jsonPath("$.statusCode").value(404));
	}

	@Test
	void shouldReturnUsersListWithDefaultPaginationWhenUsersPresent() throws Exception {
		performRequest(HttpMethod.GET, ApiPaths.USER_ENDPOINT, null).andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(4))))
			.andExpect(jsonPath("$.data.content[*].email",
					hasItems("admin@med.com", "house@med.com", "jane.doe@mail.com")))
			.andExpect(jsonPath("$.data.content[?(@.email == 'house@med.com')].firstName").value("Gregory"))
			.andExpect(jsonPath("$.data.content[?(@.email == 'house@med.com')].lastName").value("House"))
			.andExpect(jsonPath("$.data.content[?(@.email == 'house@med.com')].role").value("DOCTOR"))
			.andExpect(jsonPath("$.data.totalElements", greaterThanOrEqualTo(4)))
			.andExpect(jsonPath("$.data.totalPages", greaterThanOrEqualTo(1)))
			.andExpect(jsonPath("$.data.first").value(true));
	}

	@Test
	void shouldUpdateUserSuccessfullyWhenInputIsValid() throws Exception {
		Long targetUserId = 4L;

		UserUpdateRequestDto requestDto = new UserUpdateRequestDto("jane.updated@mail.com", "NewPassword123",
				"Jane Updated", "Doe Updated", LocalDate.of(2000, 1, 1), Status.ACTIVE, "http://new-image.com");

		performRequest(HttpMethod.PUT, ApiPaths.USER_ENDPOINT + "/{id}", requestDto, targetUserId).andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.id").value(targetUserId))
			.andExpect(jsonPath("$.data.email").value("jane.updated@mail.com"))
			.andExpect(jsonPath("$.data.firstName").value("Jane Updated"))
			.andExpect(jsonPath("$.data.lastName").value("Doe Updated"))
			.andExpect(jsonPath("$.data.status").value("ACTIVE"))
			.andExpect(jsonPath("$.data.profileUrl").value("http://new-image.com"))
			.andExpect(jsonPath("$.data.birthDate").value("2000-01-01"))
			.andExpect(jsonPath("$.data.role").value("PATIENT"));
	}

	@Test
	void shouldReturnConflictWhenEmailAlreadyExists() throws Exception {
		Long targetUserId = 4L;

		UserUpdateRequestDto requestDto = new UserUpdateRequestDto("admin@med.com", null, "Jane", "Doe",
				LocalDate.of(1990, 1, 1), Status.ACTIVE, null);

		performRequest(HttpMethod.PUT, ApiPaths.USER_ENDPOINT + "/{id}", requestDto, targetUserId).andDo(print())
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.error.errorCode").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.error.message").value("Email already exists"))
			.andExpect(jsonPath("$.status").value("409 CONFLICT"));
	}

	@Test
	void shouldReturnBadRequestWhenInputIsInvalid() throws Exception {
		Long targetUserId = 4L;

		UserUpdateRequestDto requestDto = new UserUpdateRequestDto("not-an-email", "passHash123", "FirstName",
				"LastName", LocalDate.of(2000, 1, 1), Status.ACTIVE, null);

		performRequest(HttpMethod.PUT, ApiPaths.USER_ENDPOINT + "/{id}", requestDto, targetUserId).andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error.errorCode").value("VALIDATION_ERROR"))
			.andExpect(jsonPath("$.error.message").value("email: Must be a valid email format"))
			.andExpect(jsonPath("$.status").value("400 BAD_REQUEST"))
			.andExpect(jsonPath("$.statusCode").value(400));
	}

	@Test
	void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
		Long nonExistentId = 9999L;

		UserUpdateRequestDto requestDto = new UserUpdateRequestDto("valid@mail.com", "validPass123", "Name", "Last",
				LocalDate.of(2000, 1, 1), Status.ACTIVE, null);

		performRequest(HttpMethod.PUT, ApiPaths.USER_ENDPOINT + "/{id}", requestDto, nonExistentId).andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.error.errorCode").value("NOT_FOUND"))
			.andExpect(jsonPath("$.error.message").value("User not found"))
			.andExpect(jsonPath("$.status").value("404 NOT_FOUND"))
			.andExpect(jsonPath("$.statusCode").value("404"));
	}

}
