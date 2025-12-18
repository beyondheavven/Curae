package com.vention.medfast.api.validation.validator;

import com.vention.medfast.api.validation.annotation.ExistingCitizenship;
import com.vention.medfast.country.service.CountryService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExistingCitizenshipValidator implements ConstraintValidator<ExistingCitizenship, String> {

	private final CountryService countryService;

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null || value.isBlank()) {
			return true;
		}
		return countryService.existsByCitizenshipIgnoreCase(value.trim());
	}

}
