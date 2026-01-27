package com.vention.medfast.api.validation.validator;

import com.vention.medfast.api.validation.annotation.ValidDataRange;
import com.vention.medfast.statistics.dto.request.StatisticsPeriodRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateRangeValidator implements ConstraintValidator<ValidDataRange, StatisticsPeriodRequestDto> {

	@Override
	public boolean isValid(StatisticsPeriodRequestDto value, ConstraintValidatorContext constraintValidatorContext) {
		if (value == null)
			return true;
		if (value.startDate() == null || value.endDate() == null)
			return true;

		return !value.endDate().isBefore(value.startDate());
	}

}
