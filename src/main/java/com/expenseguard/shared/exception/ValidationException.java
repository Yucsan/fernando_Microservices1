// ============= ValidationException.java =============
package com.expenseguard.shared.exception;

import java.util.List;
import java.util.Map;

public class ValidationException extends RuntimeException {
    
    private final Map<String, List<String>> fieldErrors;
    
    public ValidationException(String message, Map<String, List<String>> fieldErrors) {
        super(message);
        this.fieldErrors = fieldErrors;
    }
    
    public ValidationException(String message) {
        this(message, Map.of());
    }
    
    public Map<String, List<String>> getFieldErrors() {
        return fieldErrors;
    }
    
    public boolean hasFieldErrors() {
        return fieldErrors != null && !fieldErrors.isEmpty();
    }
}