package flowershop.accounting.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, ANNOTATION_TYPE, FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = DoubleValidator.class)
@Documented
public @interface IsDouble {

	String message() default "Value has to be numeric!";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
