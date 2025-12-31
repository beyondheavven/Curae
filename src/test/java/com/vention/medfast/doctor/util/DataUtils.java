package com.vention.medfast.doctor.util;

import com.vention.medfast.doctor.dto.DoctorProfileCreateDto;
import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.doctor.model.DoctorDetails;
import com.vention.medfast.specialization.model.Specialization;
import com.vention.medfast.user.model.RoleType;
import com.vention.medfast.user.model.Status;
import com.vention.medfast.user.model.User;

import java.time.LocalDate;
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

	public static DoctorProfileCreateDto getDoctorProfileCreateRequestDto() {
		return DoctorProfileCreateDto.builder()
			.specializationIds(Set.of(SPECIALIZATION_ID))
			.phoneNumber(PHONE_NUMBER)
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

}
