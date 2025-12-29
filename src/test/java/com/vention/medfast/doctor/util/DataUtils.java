package com.vention.medfast.doctor.util;

import com.vention.medfast.doctor.model.Doctor;
import com.vention.medfast.doctor.model.DoctorDetails;
import com.vention.medfast.user.model.RoleType;
import com.vention.medfast.user.model.User;

import java.time.LocalDate;

public class DataUtils {

	public static User getDoctorUserTransient(String email) {
		User user = new User();
		user.setEmail(email);
		user.setPasswordHash("hash_placeholder");
		user.setFirstName("Test");
		user.setLastName("Doctor");
		user.setRole(RoleType.DOCTOR);
		user.setBirthDate(LocalDate.of(1990, 1, 1));
		return user;
	}

	public static DoctorDetails getDoctorDetailsTransient(String licenseNumber) {
		DoctorDetails details = new DoctorDetails();
		details.setLicenseNumber(licenseNumber);
		details.setPhoneNumber("+15550000000");
		return details;
	}

	public static Doctor getDoctorTransient(User user, DoctorDetails details) {
		Doctor doctor = new Doctor();
		doctor.setBio("Test Bio");
		doctor.setUser(user);

		if (details != null) {
			doctor.setDetails(details);
		}

		return doctor;
	}

}
