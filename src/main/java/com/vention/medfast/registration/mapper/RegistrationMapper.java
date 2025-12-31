package com.vention.medfast.registration.mapper;

import com.vention.medfast.doctor.dto.DoctorProfileCreateDto;
import com.vention.medfast.registration.dto.DoctorRegistrationRequestDto;
import com.vention.medfast.user.dto.UserCreateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RegistrationMapper {

	@Mapping(target = "password", source = "tempPassword")
	@Mapping(target = "profileUrl", ignore = true)
	UserCreateDto toUserCreateDto(DoctorRegistrationRequestDto request, String tempPassword);

	DoctorProfileCreateDto toDoctorProfileDto(DoctorRegistrationRequestDto request);

}
