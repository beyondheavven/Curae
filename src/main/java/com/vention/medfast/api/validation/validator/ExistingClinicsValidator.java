package com.vention.medfast.api.validation.validator;

import com.vention.medfast.api.validation.annotation.ExistingClinics;
import com.vention.medfast.clinic.model.Clinic;
import com.vention.medfast.clinic.service.ClinicService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static org.springframework.util.CollectionUtils.isEmpty;

@Component
@RequiredArgsConstructor
public class ExistingClinicsValidator implements ConstraintValidator<ExistingClinics, Set<Long>> {

	private final ClinicService clinicService;

	@Override
	public boolean isValid(Set<Long> clinicIds, ConstraintValidatorContext constraintValidatorContext) {
		if (isEmpty(clinicIds)) {
			return true;
		}

		List<Clinic> clinics = clinicService.findAllByIds(clinicIds);
		return clinicIds.size() == clinics.size();
	}

}
