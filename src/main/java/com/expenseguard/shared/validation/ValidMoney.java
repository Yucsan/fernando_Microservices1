// ============= ValidMoney.java =============
package com.expenseguard.shared.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MoneyValidator.class)
public @interface ValidMoney {
    
    String message() default "Invalid money amount";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    boolean allowNegative() default false;
    
    boolean allowZero() default true;
}