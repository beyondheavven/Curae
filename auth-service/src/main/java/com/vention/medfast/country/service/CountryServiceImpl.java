package com.vention.medfast.country.service;

import com.vention.medfast.country.repository.CountryRepository;
import com.vention.medfast.country.repository.CountryZipRegexProjection;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	@Transactional(readOnly = true)
	public Optional<String> findZipRegexByCountryName(String countryName) {
		return countryRepository.findZipRegexByCountryName(countryName).map(CountryZipRegexProjection::getZipRegex);
	}

}
