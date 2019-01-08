package flowershop.accounting.form.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Cornelius Kummer
 */
@Target({TYPE, ANNOTATION_TYPE, FIELD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = LongValidator.class)
@Documented
public @interface IsLong {

	String message() default "Value has to be numeric!";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
