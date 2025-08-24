// ============= NotFoundException.java =============
package com.expenseguard.shared.exception;

public class NotFoundException extends RuntimeException {
    
    private final String resource;
    private final String identifier;
    
    public NotFoundException(String message) {
        super(message);
        this.resource = "Resource";
        this.identifier = "unknown";
    }
    
    public NotFoundException(String resource, String identifier) {
        super(String.format("%s with identifier '%s' not found", resource, identifier));
        this.resource = resource;
        this.identifier = identifier;
    }
    
    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.resource = "Resource";
        this.identifier = "unknown";
    }
    
    public String getResource() {
        return resource;
    }
    
    public String getIdentifier() {
        return identifier;
    }
}