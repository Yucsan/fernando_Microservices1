// ============= Category.java =============
package com.expenseguard.domain.model;

import com.expenseguard.domain.model.common.TransactionType;
import java.time.LocalDateTime;
import java.util.Objects;

public class Category {
    private final Long id;
    private String name;
    private String description;
    private String color;
    private String icon;
    private TransactionType type;
    private final Long userId;
    private boolean isSystem;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor para creación
    public Category(String name, String description, String color, String icon, 
                   TransactionType type, Long userId, boolean isSystem) {
        this.id = null;
        this.name = name;
        this.description = description;
        this.color = color;
        this.icon = icon;
        this.type = type;
        this.userId = userId;
        this.isSystem = isSystem;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        validate();
    }

    // Constructor para persistencia
    public Category(Long id, String name, String description, String color, String icon,
                   TransactionType type, Long userId, boolean isSystem, 
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.color = color;
        this.icon = icon;
        this.type = type;
        this.userId = userId;
        this.isSystem = isSystem;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        validate();
    }

    private void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("Category name cannot exceed 100 characters");
        }
        if (type == null) {
            throw new IllegalArgumentException("Transaction type cannot be null");
        }
        if (!isSystem && userId == null) {
            throw new IllegalArgumentException("User ID cannot be null for non-system categories");
        }
    }

    public void update(String name, String description, String color, String icon) {
        if (isSystem) {
            throw new IllegalStateException("Cannot update system categories");
        }
        this.name = name;
        this.description = description;
        this.color = color;
        this.icon = icon;
        this.updatedAt = LocalDateTime.now();
        validate();
    }

    public boolean belongsToUser(Long userId) {
        return this.userId != null && this.userId.equals(userId);
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getColor() { return color; }
    public String getIcon() { return icon; }
    public TransactionType getType() { return type; }
    public Long getUserId() { return userId; }
    public boolean isSystem() { return isSystem; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", isSystem=" + isSystem +
                '}';
    }
}