package com.vention.curae.api.validation.validator;

import com.vention.curae.api.validation.annotation.ExistingCitizenship;
import com.vention.curae.country.service.CountryService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class ExistingCitizenshipValidator implements ConstraintValidator<ExistingCitizenship, String> {

	private final CountryService countryService;

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (!StringUtils.hasText(value)) {
			return true;
		}
		return countryService.existsByCitizenshipIgnoreCase(value.trim());
	}

}
