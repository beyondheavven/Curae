package com.vention.medfast.address.mapper;

import com.vention.medfast.address.model.Address;
import com.vention.medfast.registration.dto.AddressDto;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class AddressMapperTest {

	private final AddressMapper mapper = Mappers.getMapper(AddressMapper.class);

	@Test
	void shouldMapAddressDtoToEntityWhenInputIsValid() {
		AddressDto dto = new AddressDto("Main Street", "Metropolis", "12B", "42", "State", "12345", "+123456789");

		Address address = mapper.toEntity(dto);

		assertThat(address).isNotNull();
		assertThat(address.getId()).isNull();
		assertThat(address.getStreetName()).isEqualTo(dto.getStreet());
		assertThat(address.getCityName()).isEqualTo(dto.getCity());
		assertThat(address.getApartmentNumber()).isEqualTo(dto.getApartment());
		assertThat(address.getHouseNumber()).isEqualTo(dto.getHouseNumber());
		assertThat(address.getStateName()).isEqualTo(dto.getState());
		assertThat(address.getZipCode()).isEqualTo(dto.getZipCode());
		assertThat(address.getPhone()).isEqualTo(dto.getPhoneNumber());
	}

	@Test
	void shouldReturnNullWhenDtoIsNull() {
		assertThat(mapper.toEntity(null)).isNull();
	}

}
