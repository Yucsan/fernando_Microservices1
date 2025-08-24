package com.expenseguard.shared.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {
        logger.warn("Resource not found: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Not Found")
            .message(ex.getMessage())
            .path(getCurrentPath())
            .build();
            
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        logger.warn("Business exception: {} - Code: {}", ex.getMessage(), ex.getErrorCode());
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Business Error")
            .message(ex.getMessage())
            .errorCode(ex.getErrorCode())
            .path(getCurrentPath())
            .build();
            
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {
        logger.warn("Validation exception: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Validation Error")
            .message(ex.getMessage())
            .fieldErrors(ex.getFieldErrors())
            .path(getCurrentPath())
            .build();
            
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        logger.warn("Validation failed for request");
        
        Map<String, List<String>> fieldErrors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .collect(Collectors.groupingBy(
                FieldError::getField,
                Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
            ));
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Validation Failed")
            .message("Invalid request data")
            .fieldErrors(fieldErrors)
            .path(getCurrentPath())
            .build();
            
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("Illegal argument: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Invalid Argument")
            .message(ex.getMessage())
            .path(getCurrentPath())
            .build();
            
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        logger.error("Unexpected error occurred", ex);
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error("Internal Server Error")
            .message("An unexpected error occurred")
            .path(getCurrentPath())
            .build();
            
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
    
    private String getCurrentPath() {
        // En una implementación real, obtendrías el path del request actual
        return "/api/v1";
    }
    
    // DTO para respuestas de error
    public static class ErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String errorCode;
        private String path;
        private Map<String, List<String>> fieldErrors;
        
        // Constructor privado para usar el builder
        private ErrorResponse() {}
        
        public static Builder builder() {
            return new Builder();
        }
        
        public static class Builder {
            private final ErrorResponse errorResponse = new ErrorResponse();
            
            public Builder timestamp(LocalDateTime timestamp) {
                errorResponse.timestamp = timestamp;
                return this;
            }
            
            public Builder status(int status) {
                errorResponse.status = status;
                return this;
            }
            
            public Builder error(String error) {
                errorResponse.error = error;
                return this;
            }
            
            public Builder message(String message) {
                errorResponse.message = message;
                return this;
            }
            
            public Builder errorCode(String errorCode) {
                errorResponse.errorCode = errorCode;
                return this;
            }
            
            public Builder path(String path) {
                errorResponse.path = path;
                return this;
            }
            
            public Builder fieldErrors(Map<String, List<String>> fieldErrors) {
                errorResponse.fieldErrors = fieldErrors;
                return this;
            }
            
            public ErrorResponse build() {
                if (errorResponse.fieldErrors == null) {
                    errorResponse.fieldErrors = new HashMap<>();
                }
                return errorResponse;
            }
        }
        
        // Getters
        public LocalDateTime getTimestamp() { return timestamp; }
        public int getStatus() { return status; }
        public String getError() { return error; }
        public String getMessage() { return message; }
        public String getErrorCode() { return errorCode; }
        public String getPath() { return path; }
        public Map<String, List<String>> getFieldErrors() { return fieldErrors; }
    }
}