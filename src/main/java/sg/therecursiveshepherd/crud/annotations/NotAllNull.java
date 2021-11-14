package sg.therecursiveshepherd.crud.annotations;

import sg.therecursiveshepherd.crud.services.validators.NotAllNullConstraintValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotAllNullConstraintValidator.class)
@Documented
public @interface NotAllNull {

  String message() default "Invalid input - at least one field must not be null";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String[] fieldNames() default {};

}
