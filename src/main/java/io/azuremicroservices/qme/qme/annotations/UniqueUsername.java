package io.azuremicroservices.qme.qme.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import io.azuremicroservices.qme.qme.validators.UniqueUsernameValidator;

@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueUsernameValidator.class)
public @interface UniqueUsername {
	String message() default "Username already exists";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
