// ============= MoneyValidator.java =============
package com.expenseguard.shared.validation;

import com.expenseguard.domain.model.common.Money;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

public class MoneyValidator implements ConstraintValidator<ValidMoney, Money> {
    
    private boolean allowNegative;
    private boolean allowZero;
    
    @Override
    public void initialize(ValidMoney constraintAnnotation) {
        this.allowNegative = constraintAnnotation.allowNegative();
        this.allowZero = constraintAnnotation.allowZero();
    }
    
    @Override
    public boolean isValid(Money money, ConstraintValidatorContext context) {
        if (money == null) {
            return true; // Use @NotNull for null validation
        }
        
        BigDecimal amount = money.getAmount();
        
        if (!allowZero && amount.signum() == 0) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Amount cannot be zero")
                   .addConstraintViolation();
            return false;
        }
        
        if (!allowNegative && amount.signum() < 0) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Amount cannot be negative")
                   .addConstraintViolation();
            return false;
        }
        
        return true;
    }
}