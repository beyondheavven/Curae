package com.vention.medfast.registration.util;

import com.vention.medfast.registration.dto.DoctorRegistrationRequestDto;

import java.time.LocalDate;
import java.util.Set;

public class DataUtils {

	public static DoctorRegistrationRequestDto getDoctorRegistrationRequestDto() {
		return DoctorRegistrationRequestDto.builder()
			.firstName("Gregory")
			.lastName("House")
			.email("house@med.com")
			.phoneNumber("+1234567")
			.birthDate(LocalDate.of(2000, 1, 1))
			.specializationIds(Set.of(1L, 2L))
			.licenseNumber("1234567")
			.build();
	}

}
