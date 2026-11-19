package com.vention.curae.user.mapper;

import com.vention.curae.registration.dto.DoctorRegistrationRequestDto;
import com.vention.curae.registration.dto.PersonDto;
import com.vention.curae.user.dto.UserResponseDto;
import com.vention.curae.user.dto.UserUpdateRequestDto;
import com.vention.curae.user.model.User;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring")
public interface UserMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "passwordHash", ignore = true)
	@Mapping(target = "role", ignore = true)
	@Mapping(target = "refreshTokens", ignore = true)
	void updateUserFromDto(@MappingTarget User user, UserUpdateRequestDto dto,
			@Context PasswordEncoder passwordEncoder);

	@AfterMapping
	default void updatePassword(@MappingTarget User user, UserUpdateRequestDto dto,
			@Context PasswordEncoder passwordEncoder) {
		if (dto.password() != null && !dto.password().isBlank()) {
			user.setPasswordHash(passwordEncoder.encode(dto.password()));
		}
	}

	UserResponseDto toResponseDto(User user);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "passwordHash", ignore = true)
	@Mapping(target = "role", constant = "PATIENT")
	@Mapping(target = "status", constant = "UNVERIFIED")
	@Mapping(target = "firstName", source = "name")
	@Mapping(target = "birthDate", source = "birthDate")
	User toPatientUser(PersonDto dto, @Context PasswordEncoder passwordEncoder);

	@AfterMapping
	default void encodePassword(@MappingTarget User user, PersonDto dto, @Context PasswordEncoder passwordEncoder) {
		user.setPasswordHash(passwordEncoder.encode(dto.getRawPassword()));
	}

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "passwordHash", ignore = true)
	@Mapping(target = "status", constant = "UNVERIFIED")
	@Mapping(target = "role", constant = "DOCTOR")
	User toDoctorUser(DoctorRegistrationRequestDto dto, String tempPassword, @Context PasswordEncoder passwordEncoder);

	@AfterMapping
	default void encodePassword(@MappingTarget User user, String tempPassword,
			@Context PasswordEncoder passwordEncoder) {
		user.setPasswordHash(passwordEncoder.encode(tempPassword));
	}

}
