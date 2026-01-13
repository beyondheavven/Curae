package com.vention.medfast.api.validation.annotation;

import com.vention.medfast.api.validation.validator.ValidZipCodeByCountryValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidZipCodeByCountryValidator.class)
public @interface ValidZipCodeByCountry {

	String message() default "Invalid zip code for selected country";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
