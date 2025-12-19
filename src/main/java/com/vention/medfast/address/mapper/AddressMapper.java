package com.vention.medfast.address.mapper;

import com.vention.medfast.address.model.Address;
import com.vention.medfast.registration.dto.AddressDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AddressMapper {

	@Mapping(target = "phone", source = "phoneNumber")
	Address toEntity(AddressDto dto);

}
