package com.vention.medfast.api.validation.validator;

import com.vention.medfast.api.validation.annotation.ValidPassword;
import com.vention.medfast.api.validation.properties.PasswordValidationProperties;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidPasswordValidator implements ConstraintValidator<ValidPassword, String> {

	private final PasswordValidationProperties properties;

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null || value.isBlank()) {
			return true;
		}

		return Pattern.matches(properties.getRegex(), value);
	}

}
