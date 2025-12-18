package com.vention.medfast.patient.mapper;

import com.vention.medfast.address.model.Address;
import com.vention.medfast.patient.model.Patient;
import com.vention.medfast.registration.dto.PersonDto;
import com.vention.medfast.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PatientMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "status", constant = "UNVERIFIED")
	@Mapping(target = "user", source = "user")
	@Mapping(target = "address", source = "address")
	@Mapping(target = "dateOfBirth", source = "dto.birthDate")
	@Mapping(target = "gender", source = "dto.gender")
	@Mapping(target = "citizenship", source = "dto.citizenship")
	Patient toEntity(PersonDto dto, User user, Address address);

}
