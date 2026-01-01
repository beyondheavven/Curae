package com.vention.medfast.api.validation.validator;

import com.vention.medfast.api.validation.annotation.ValidZipCodeByCountry;
import com.vention.medfast.country.service.CountryService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Optional;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidZipCodeByCountryValidator implements ConstraintValidator<ValidZipCodeByCountry, ZipCountryAware> {

	private final CountryService countryService;

	@Override
	public boolean isValid(ZipCountryAware value, ConstraintValidatorContext context) {
		if (value == null)
			return true;

		String countryName = value.getState();
		String zip = value.getZipCode();

		if (countryName == null || countryName.isBlank() || zip == null || zip.isBlank()) {
			return true;
		}

		String countryKey = countryName.trim();
		String zipValue = zip.trim();

		Optional<String> regexOpt = countryService.findZipRegexByCountryName(countryKey);

		if (regexOpt.isEmpty()) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("Unknown country")
				.addPropertyNode("state")
				.addConstraintViolation();
			return false;
		}

		if (!Pattern.matches(regexOpt.get(), zipValue)) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("Invalid zip code for selected country")
				.addPropertyNode("zipCode")
				.addConstraintViolation();
			return false;
		}

		return true;
	}

}
