package com.vention.curae.api.validation.validator;

import com.vention.curae.api.validation.annotation.ValidPassword;
import com.vention.curae.api.validation.properties.PasswordValidationProperties;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
@RequiredArgsConstructor
public class ValidPasswordValidator implements ConstraintValidator<ValidPassword, String> {

	private final PasswordValidationProperties properties;

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (isBlank(value)) {
			return true;
		}

		return Pattern.matches(properties.getRegex(), value);
	}

}
