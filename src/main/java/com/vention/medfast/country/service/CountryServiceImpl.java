package com.vention.medfast.country.service;

import com.vention.medfast.country.repository.CountryRepository;
import com.vention.medfast.country.repository.CountryZipRegexProjection;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class CountryServiceImpl implements CountryService {

	private final CountryRepository countryRepository;

	@Override
	@Transactional(readOnly = true)
	public boolean existsByCitizenshipIgnoreCase(String citizenship) {
		return countryRepository.existsByCitizenshipIgnoreCase(citizenship);
	}

	@Override
	public Optional<String> findZipRegexByCountryName(String countryName) {
		return countryRepository.findZipRegexByCountryName(countryName).map(CountryZipRegexProjection::getZipRegex);
	}

}
