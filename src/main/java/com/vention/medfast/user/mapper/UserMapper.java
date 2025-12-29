package com.vention.medfast.user.mapper;

import com.vention.medfast.registration.dto.PersonDto;
import com.vention.medfast.user.model.User;
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
	@Mapping(target = "role", constant = "PATIENT")
	@Mapping(target = "firstName", source = "name")
	@Mapping(target = "birthDate", source = "birthDate")
	User toPatientUser(PersonDto dto, @Context PasswordEncoder passwordEncoder);

	@AfterMapping
	default void encodePassword(@MappingTarget User user, PersonDto dto, @Context PasswordEncoder passwordEncoder) {
		user.setPasswordHash(passwordEncoder.encode(dto.getRawPassword()));
	}

}
