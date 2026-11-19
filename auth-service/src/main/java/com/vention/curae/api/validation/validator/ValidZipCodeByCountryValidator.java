package com.vention.curae.api.validation.validator;

import com.vention.curae.api.validation.annotation.ValidZipCodeByCountry;
import com.vention.curae.country.service.CountryService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Optional;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class ValidZipCodeByCountryValidator implements ConstraintValidator<ValidZipCodeByCountry, ZipCountryAware> {

	private final CountryService countryService;

	@Override
	public boolean isValid(ZipCountryAware value, ConstraintValidatorContext context) {
		if (value == null || !StringUtils.hasText(value.getState()) || !StringUtils.hasText(value.getZipCode())) {
			return true;
		}

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
