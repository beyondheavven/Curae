package com.vention.medfast.api.validation.annotation;

import com.vention.medfast.api.validation.validator.ExistingCitizenshipValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExistingCitizenshipValidator.class)
public @interface ExistingCitizenship {

	String message() default "Unknown citizenship";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
