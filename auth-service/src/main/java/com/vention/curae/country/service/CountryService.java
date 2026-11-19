package com.vention.curae.country.service;

import com.vention.curae.country.dto.CountryResponseDto;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CountryService {

	boolean existsByCitizenshipIgnoreCase(String citizenship);

	Optional<String> findZipRegexByCountryName(String countryName);

	Page<CountryResponseDto> getAll(Pageable pageable);

}
