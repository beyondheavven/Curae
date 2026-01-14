package com.vention.medfast.api.validation.annotation;

import com.vention.medfast.api.validation.validator.ExistingClinicsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExistingClinicsValidator.class)
public @interface ExistingClinics {

	String message() default "Invalid clinic ids, one or more clinics ids do not exist";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
