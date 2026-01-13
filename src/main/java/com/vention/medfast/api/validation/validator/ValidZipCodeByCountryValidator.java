package com.vention.medfast.api.validation.validator;

import com.vention.medfast.api.validation.annotation.ValidZipCodeByCountry;
import com.vention.medfast.country.service.CountryService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
@RequiredArgsConstructor
public class ValidZipCodeByCountryValidator implements ConstraintValidator<ValidZipCodeByCountry, ZipCountryAware> {

	private final CountryService countryService;

	@Override
	public boolean isValid(ZipCountryAware value, ConstraintValidatorContext context) {
		if (value == null || (isBlank(value.getState())) || (isBlank(value.getZipCode())))
			return true;

		String countryName = value.getState().trim();
		String zip = value.getZipCode().trim();

		Optional<String> regexOpt = countryService.findZipRegexByCountryName(countryName);

		if (regexOpt.isEmpty()) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("Unknown country")
				.addPropertyNode("state")
				.addConstraintViolation();
			return false;
		}

		if (!Pattern.matches(regexOpt.get(), zip)) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("Invalid zip code for selected country")
				.addPropertyNode("zipCode")
				.addConstraintViolation();
			return false;
		}

		return true;
	}

}
