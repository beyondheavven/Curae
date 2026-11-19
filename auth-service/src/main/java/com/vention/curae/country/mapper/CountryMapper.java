package com.vention.curae.country.mapper;

import com.vention.curae.country.dto.CountryResponseDto;
import com.vention.curae.country.model.Country;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CountryMapper {

	CountryResponseDto toDto(Country entity);

}
