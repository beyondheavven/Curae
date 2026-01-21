package com.vention.medfast.country.service;

import com.vention.medfast.country.dto.CountryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CountryService {

	boolean existsByCitizenshipIgnoreCase(String citizenship);

	Optional<String> findZipRegexByCountryName(String countryName);

	Page<CountryResponseDto> getAll(Pageable pageable);

}
