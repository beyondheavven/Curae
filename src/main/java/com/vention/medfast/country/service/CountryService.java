package com.vention.medfast.country.service;

import java.util.Optional;

public interface CountryService {

	boolean existsByCitizenshipIgnoreCase(String citizenship);

	Optional<String> findZipRegexByCountryName(String countryName);

}
