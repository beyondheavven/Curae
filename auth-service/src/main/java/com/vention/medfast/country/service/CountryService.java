package com.vention.medfast.country.service;

import com.vention.medfast.country.dto.CountryResponseDto;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CountryService {

	boolean existsByCitizenshipIgnoreCase(String citizenship);

	Optional<String> findZipRegexByCountryName(String countryName);

	Page<CountryResponseDto> getAll(Pageable pageable);

}
