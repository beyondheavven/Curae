package com.vention.medfast.api.validation.validator;

import com.vention.medfast.api.validation.annotation.ExistingSpecializations;
import com.vention.medfast.specialization.model.Specialization;
import com.vention.medfast.specialization.service.SpecializationService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ExistingSpecializationsValidator implements ConstraintValidator<ExistingSpecializations, Set<Long>> {

	private final SpecializationService specializationService;

	@Override
	public boolean isValid(Set<Long> specializationIds, ConstraintValidatorContext constraintValidatorContext) {
		if (specializationIds == null || specializationIds.isEmpty()) {
			return true;
		}

		List<Specialization> specializations = specializationService.findAllByIds(specializationIds);
		return specializationIds.size() == specializations.size();
	}

}
