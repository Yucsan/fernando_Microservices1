// ============= User.java =============
package com.expenseguard.domain.model;

import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Objects;

public class User {
    private final Long id;
    private String email;
    private String firstName;
    private String lastName;
    private Currency defaultCurrency;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor para creación
    public User(String email, String firstName, String lastName, Currency defaultCurrency) {
        this.id = null;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.defaultCurrency = defaultCurrency;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        validate();
    }

    // Constructor para persistencia
    public User(Long id, String email, String firstName, String lastName, 
                Currency defaultCurrency, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.defaultCurrency = defaultCurrency;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        validate();
    }

    private void validate() {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        if (defaultCurrency == null) {
            throw new IllegalArgumentException("Default currency cannot be null");
        }
    }

    public void updateProfile(String firstName, String lastName, Currency defaultCurrency) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.defaultCurrency = defaultCurrency;
        this.updatedAt = LocalDateTime.now();
        validate();
    }

    public String getFullName() {
        return firstName + (lastName != null ? " " + lastName : "");
    }

    // Getters
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public Currency getDefaultCurrency() { return defaultCurrency; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", fullName='" + getFullName() + '\'' +
                ", defaultCurrency=" + defaultCurrency.getCurrencyCode() +
                '}';
    }
}