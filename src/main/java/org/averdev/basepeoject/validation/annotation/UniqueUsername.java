package org.averdev.basepeoject.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.averdev.basepeoject.common.validator.UniqueUsernameValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueUsernameValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueUsername {
    
    String message() default "Username is already taken";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
