package com.vention.medfast.address.mapper;

import com.vention.medfast.address.model.Address;
import com.vention.medfast.registration.dto.AddressDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AddressMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "streetName", source = "street")
	@Mapping(target = "cityName", source = "city")
	@Mapping(target = "apartmentNumber", source = "apartment")
	@Mapping(target = "stateName", source = "state")
	@Mapping(target = "phone", source = "phoneNumber")
	Address toEntity(AddressDto dto);

}
