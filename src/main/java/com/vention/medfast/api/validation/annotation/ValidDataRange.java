package com.vention.medfast.api.validation.annotation;

import com.vention.medfast.api.validation.validator.DateRangeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateRangeValidator.class)
public @interface ValidDataRange {

	String message() default "End date must be before start date";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
