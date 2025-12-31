package com.vention.medfast.patient.mapper;

import com.vention.medfast.address.dto.AddressCreateDto;
import com.vention.medfast.address.model.Address;
import com.vention.medfast.patient.dto.PatientCreateDto;
import com.vention.medfast.patient.dto.PatientResponseDto;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.registration.dto.PersonDto;
import com.vention.medfast.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PatientMapper {

	Patient toEntity(PatientCreateDto dto);

	@Mapping(source = "user.firstName", target = "firstName")
	@Mapping(source = "user.lastName", target = "lastName")
	@Mapping(source = "user.profileUrl", target = "profileUrl")
	PatientResponseDto toDto(Patient entity);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "gender", source = "dto.gender")
	@Mapping(target = "citizenship", source = "dto.citizenship")
	Patient toEntity(PersonDto dto, User user, Address address);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "refreshTokens", ignore = true)
	@Mapping(target = "role", constant = "PATIENT")
	@Mapping(target = "status", constant = "ACTIVE")
	@Mapping(target = "firstName", source = "user.firstName")
	@Mapping(target = "lastName", source = "user.lastName")
	@Mapping(target = "email", source = "user.email")
	@Mapping(target = "passwordHash", source = "user.password")
	@Mapping(target = "profileUrl", source = "user.profileUrl")
	@Mapping(target = "birthDate", source = "user.birthDate")
	User toUser(PatientCreateDto dto);

	@Mapping(target = "id", ignore = true)
	Address toAddress(AddressCreateDto dto);

}
