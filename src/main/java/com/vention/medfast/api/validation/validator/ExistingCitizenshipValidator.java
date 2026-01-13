package com.vention.medfast.api.validation.validator;

import com.vention.medfast.api.validation.annotation.ExistingCitizenship;
import com.vention.medfast.country.service.CountryService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
@RequiredArgsConstructor
public class ExistingCitizenshipValidator implements ConstraintValidator<ExistingCitizenship, String> {

	private final CountryService countryService;

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (isBlank(value)) {
			return true;
		}
		return countryService.existsByCitizenshipIgnoreCase(value.trim());
	}

}
