package com.vention.medfast.api.validation.annotation;

import com.vention.medfast.api.validation.validator.ValidPasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@ReportAsSingleViolation
@Constraint(validatedBy = ValidPasswordValidator.class)
public @interface ValidPassword {

	String message() default "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, and one digit";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
