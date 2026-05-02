package org.averdev.basepeoject.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.averdev.basepeoject.common.validator.UniqueEmailValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueEmailValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueEmail {
    
    String message() default "Email is already in use";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
