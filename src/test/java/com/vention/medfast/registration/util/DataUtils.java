package com.vention.medfast.registration.util;

import com.vention.medfast.doctor.dto.DoctorProfileCreateDto;
import com.vention.medfast.registration.dto.DoctorRegistrationRequestDto;
import com.vention.medfast.user.dto.UserCreateDto;

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

	public static UserCreateDto getUserCreateDto() {
		return UserCreateDto.builder()
			.firstName("Gregory")
			.lastName("House")
			.email("house@med.com")
			.password("DummyPass123!")
			.birthDate(LocalDate.of(2000, 1, 1))
			.build();
	}

	public static DoctorProfileCreateDto getDoctorProfileCreateDto() {
		return DoctorProfileCreateDto.builder()
			.licenseNumber("1234567")
			.phoneNumber("+1234567")
			.specializationIds(Set.of(1L, 2L))
			.build();
	}

}
