package com.vention.medfast.country.mapper;

import com.vention.medfast.country.dto.CountryResponseDto;
import com.vention.medfast.country.model.Country;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

public class CountryMapperTest {

	private final CountryMapper mapper = Mappers.getMapper(CountryMapper.class);

	@Test
	void shouldMapCountryToCountryResponseDtoWhenValidInput() {
		Country country = new Country();
		country.setId(1L);
		country.setIsoCode("US");
		country.setName("United States");
		country.setCitizenship("American");
		country.setZipRegex("^\\d{5}$");
		country.setZipExample("12345");

		CountryResponseDto dto = mapper.toDto(country);

		assertThat(dto).isNotNull();
		assertThat(dto.id()).isEqualTo(1L);
		assertThat(dto.isoCode()).isEqualTo("US");
		assertThat(dto.name()).isEqualTo("United States");
		assertThat(dto.citizenship()).isEqualTo("American");
		assertThat(dto.zipRegex()).isEqualTo("^\\d{5}$");
		assertThat(dto.zipExample()).isEqualTo("12345");
	}

	@Test
	void shouldReturnNullWhenCountryIsNull() {
		assertThat(mapper.toDto(null)).isNull();
	}

}
