package com.vention.medfast.api.validation.annotation;

import com.vention.medfast.api.validation.validator.ExistingSpecializationsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExistingSpecializationsValidator.class)
public @interface ExistingSpecializations {

	String message() default "Invalid specialization ids, one or more specializations ids do not exist";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
