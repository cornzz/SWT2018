package flowershop.user.form.validation;

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
@Target({TYPE, ANNOTATION_TYPE, FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = OldPasswordValidator.class)
@Documented
public @interface ValidOldPassword {

	String message() default "Old password incorrect!";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}