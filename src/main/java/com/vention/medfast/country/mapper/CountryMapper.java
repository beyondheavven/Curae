package com.vention.medfast.country.mapper;

import com.vention.medfast.country.dto.CountryResponseDto;
import com.vention.medfast.country.model.Country;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CountryMapper {

	CountryResponseDto toDto(Country entity);

}
