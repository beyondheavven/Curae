package com.vention.medfast.clinic.mapper;

import com.vention.medfast.address.dto.ClinicAddressResponseDto;
import com.vention.medfast.address.model.Address;
import com.vention.medfast.clinic.dto.ClinicRequestDto;
import com.vention.medfast.clinic.dto.ClinicResponseDto;
import com.vention.medfast.clinic.model.Clinic;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ClinicMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "address", ignore = true)
	@Mapping(target = "doctors", ignore = true)
	Clinic toEntity(ClinicRequestDto dto);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "doctors", ignore = true)
	@Mapping(target = "address", source = "address")
	Clinic toEntityWithAddress(ClinicRequestDto dto);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "address", ignore = true)
	@Mapping(target = "doctors", ignore = true)
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateEntity(ClinicRequestDto dto, @MappingTarget Clinic clinic);

	@Mapping(target = "id", ignore = true)
	Address toAddress(com.vention.medfast.address.dto.ClinicAddressRequestDto dto);

	@Mapping(target = "id", ignore = true)
	void updateAddress(com.vention.medfast.address.dto.ClinicAddressRequestDto dto, @MappingTarget Address address);

	ClinicResponseDto toResponseDto(Clinic clinic);

	ClinicAddressResponseDto toAddressResponseDto(Address address);

}
