package com.vention.medfast.registration.controller;

import com.vention.medfast.AbstractControllerIntegrationTest;
import com.vention.medfast.constants.ApiPaths;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.patient.model.Gender;
import com.vention.medfast.registration.dto.AddressDto;
import com.vention.medfast.registration.dto.DoctorRegistrationRequestDto;
import com.vention.medfast.registration.dto.PatientRegistrationRequestDto;
import com.vention.medfast.registration.dto.PersonDto;
import com.vention.medfast.user.model.RoleType;
import com.vention.medfast.user.model.User;
import java.time.LocalDate;
import java.util.Set;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RegistrationControllerTest extends AbstractControllerIntegrationTest {

	@Test
	void shouldRegisterPatient_WhenRequestIsValid() throws Exception {
		String email = "newpatient@example.com";
		PatientRegistrationRequestDto request = createPatientRequest(email);

		performRequest(HttpMethod.POST, ApiPaths.REGISTRATION_ENDPOINT, request).andExpect(status().isCreated());

		User user = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
			.setParameter("email", email)
			.getSingleResult();

		assertThat(user).isNotNull();
		assertThat(user.getRole()).isEqualTo(RoleType.PATIENT);
	}

	@Test
	void shouldRegisterDoctor_WhenUserIsAdmin() throws Exception {
		String doctorEmail = "new.doctor@med.com";
		DoctorRegistrationRequestDto request = createDoctorRequest(doctorEmail);

		performRequest(HttpMethod.POST,
				ApiPaths.REGISTRATION_ENDPOINT + ApiPaths.DOCTOR_ENDPOINT + ApiPaths.ADMIN_ENDPOINT, request)
			.andExpect(status().isCreated());

		Doctor doctor = em.createQuery("SELECT d FROM Doctor d WHERE d.user.email =:email", Doctor.class)
			.setParameter("email", doctorEmail)
			.getSingleResult();

		assertThat(doctor).isNotNull();
		assertThat(doctor.getUser().getRole()).isEqualTo(RoleType.DOCTOR);
		assertThat(doctor.getUser().getFirstName()).isEqualTo("Test first");
		assertThat(doctor.getUser().getLastName()).isEqualTo("Test last");
		assertThat(doctor.getSpecializations().size()).isEqualTo(1);
	}

	@Test
	void shouldReturn400_WhenRegistrationFieldsAreEmptyForPatient() throws Exception {
		PatientRegistrationRequestDto request = createPatientRequest(null);

		performRequest(HttpMethod.POST, ApiPaths.REGISTRATION_ENDPOINT, request).andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error.message").value("Validation failed"))
			.andExpect(jsonPath("$.error.validationErrors").isArray());
	}

	@Test
	void shouldReturn400_WhenRegistrationFieldsAreEmptyForDoctor() throws Exception {
		DoctorRegistrationRequestDto request = createDoctorRequest(null);

		performRequest(HttpMethod.POST,
				ApiPaths.REGISTRATION_ENDPOINT + ApiPaths.DOCTOR_ENDPOINT + ApiPaths.ADMIN_ENDPOINT, request)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error.message").value("Validation failed"))
			.andExpect(jsonPath("$.error.validationErrors").isArray());
	}

	@Test
	void shouldReturn409_WhenPatientEmailAlreadyExists() throws Exception {
		String existingEmail = "jane.doe@mail.com";
		PatientRegistrationRequestDto request = createPatientRequest(existingEmail);

		performRequest(HttpMethod.POST, ApiPaths.REGISTRATION_ENDPOINT, request).andExpect(status().isConflict());
	}

	private PatientRegistrationRequestDto createPatientRequest(String email) {
		PersonDto person = new PersonDto("John", "Doe", LocalDate.of(1990, 5, 15), Gender.MALE, "StrongPass123!", email,
				"French");

		AddressDto address = new AddressDto("Test", "Test", "12", "8", "France", "75001", "+375291234567");

		return new PatientRegistrationRequestDto(person, address);
	}

	private DoctorRegistrationRequestDto createDoctorRequest(String email) {
		return new DoctorRegistrationRequestDto("Test first", "Test last", LocalDate.of(1990, 5, 15), Set.of(1L),
				"pl8667473", email, "+64281343");
	}

}
