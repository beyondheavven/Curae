package com.vention.medfast.doctor.util;

import com.vention.medfast.doctor.dto.DoctorResponseDto;
import com.vention.medfast.doctor.dto.DoctorUpdateDto;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.doctor.model.DoctorDetails;
import com.vention.medfast.registration.dto.DoctorRegistrationRequestDto;
import com.vention.medfast.specialization.model.Specialization;
import com.vention.medfast.user.model.RoleType;
import com.vention.medfast.user.model.Status;
import com.vention.medfast.user.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class DataUtils {

	public static final String EMAIL = "john@med.com";

	public static final String FIRST_NAME = "John";

	public static final String LAST_NAME = "Doe";

	public static final String PHONE_NUMBER = "+12345678910";

	public static final String LICENSE_NUMBER = "1234567";

	public static final LocalDate BIRTH_DATE = LocalDate.of(2000, 1, 1);

	public static final Long USER_ID = 1L;

	public static final Long DOCTOR_ID = 3L;

	public static final Long SPECIALIZATION_ID = 5L;

	public static final String SPECIALIZATION_NAME = "Cardiology";

	public static final Long DR_HOUSE_ID = 1L;

	public static final Long DR_WATSON_ID = 2L;

	public static final String HOUSE_EMAIL = "house@med.com";

	public static final String ADMIN_EMAIL = "admin@med.com";

	public static final String ADMIN_PASSWORD = "pass";

	public static DoctorRegistrationRequestDto getDoctorRegistrationRequestDto() {
		return DoctorRegistrationRequestDto.builder()
			.firstName(FIRST_NAME)
			.lastName(LAST_NAME)
			.email(EMAIL)
			.phoneNumber(PHONE_NUMBER)
			.birthDate(BIRTH_DATE)
			.specializationIds(Set.of(SPECIALIZATION_ID))
			.licenseNumber(LICENSE_NUMBER)
			.build();
	}

	public static User getDoctorUserPersisted() {
		User user = new User();
		user.setId(USER_ID);
		user.setFirstName(FIRST_NAME);
		user.setLastName(LAST_NAME);
		user.setEmail(EMAIL);
		user.setPasswordHash("encoded_temp_password");
		user.setRole(RoleType.DOCTOR);
		user.setBirthDate(BIRTH_DATE);
		return user;
	}

	public static User getDoctorUserTransient() {
		User user = new User();
		user.setFirstName(FIRST_NAME);
		user.setLastName(LAST_NAME);
		user.setEmail(EMAIL);
		user.setPasswordHash("encoded_temp_password");
		user.setRole(RoleType.DOCTOR);
		user.setBirthDate(BIRTH_DATE);
		user.setStatus(Status.UNVERIFIED);
		return user;
	}

	public static Specialization getSpecializationCardiology() {
		Specialization specialization = new Specialization();
		specialization.setId(SPECIALIZATION_ID);
		specialization.setName(SPECIALIZATION_NAME);
		return specialization;
	}

	public static DoctorDetails getDoctorDetailsTransient() {
		DoctorDetails details = new DoctorDetails();
		details.setLicenseNumber(LICENSE_NUMBER);
		details.setPhoneNumber(PHONE_NUMBER);
		return details;
	}

	public static Doctor getDoctorPersisted() {
		Doctor doctor = new Doctor();
		doctor.setId(DOCTOR_ID);
		doctor.setUser(getDoctorUserPersisted());

		DoctorDetails doctorDetails = getDoctorDetailsTransient();
		doctor.setDetails(doctorDetails);

		doctor.setBio("Specialist in " + SPECIALIZATION_NAME);
		doctor.addSpecialization(getSpecializationCardiology());

		return doctor;
	}

	public static Doctor getDoctorForIntegration(User savedUser, Specialization existingSpecialization) {
		Doctor doctor = new Doctor();
		doctor.setUser(savedUser);

		DoctorDetails details = new DoctorDetails();
		details.setLicenseNumber("1432567");
		details.setPhoneNumber("+111222333");
		doctor.setDetails(details);

		doctor.setBio("Integration Test Bio");

		if (existingSpecialization != null) {
			doctor.addSpecialization(existingSpecialization);
		}

		return doctor;
	}

	public static DoctorResponseDto getDoctorResponseDto() {
		return DoctorResponseDto.builder()
			.id(DOCTOR_ID)
			.firstName(FIRST_NAME)
			.lastName(LAST_NAME)
			.email(EMAIL)
			.licenseNumber(LICENSE_NUMBER)
			.phoneNumber(PHONE_NUMBER)
			.bio("Specialist in " + SPECIALIZATION_NAME)
			.specializations(List.of())
			.clinics(List.of())
			.build();
	}

	public static DoctorUpdateDto getDoctorUpdateDto() {
		return DoctorUpdateDto.builder()
			.firstName("UpdatedJohn")
			.lastName("UpdatedDoe")
			.phoneNumber("+1987654321")
			.licenseNumber("7654321")
			.specializationIds(Set.of(10L))
			.clinicIds(Set.of(20L))
			.build();
	}

	public static DoctorUpdateDto getDoctorUpdateDtoIntegration() {
		return DoctorUpdateDto.builder()
			.firstName("GregoryUPDATED")
			.lastName("HouseUPDATED")
			.bio("Updated bio")
			.phoneNumber("+19998887777")
			.licenseNumber("LIC-99999")
			.specializationIds(Set.of(2L))
			.clinicIds(Set.of(1L))
			.build();
	}

}
