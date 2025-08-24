// ============= Transaction.java - CORREGIDA =============
package com.expenseguard.domain.model;

import com.expenseguard.domain.model.common.Money;
import com.expenseguard.domain.model.common.TransactionType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Transaction {
    private final Long id;
    private String description;
    private Money amount;
    private TransactionType type;
    private LocalDate transactionDate;
    private final Long userId;
    private Long categoryId;
    private String notes;
    private String location;
    private String merchantName;
    private String tags;
    private boolean isRecurring;
    private String externalId; // Para integraciones bancarias
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor para creación
    public Transaction(String description, Money amount, TransactionType type, 
                      LocalDate transactionDate, Long userId, Long categoryId, 
                      String notes, String location, String merchantName) {
        this.id = null;
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.transactionDate = transactionDate;
        this.userId = userId;
        this.categoryId = categoryId;
        this.notes = notes;
        this.location = location;
        this.merchantName = merchantName;
        this.tags = null;
        this.isRecurring = false;
        this.externalId = null;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        validate();
    }

    // Constructor completo para persistencia
    public Transaction(Long id, String description, Money amount, TransactionType type,
                      LocalDate transactionDate, Long userId, Long categoryId, String notes,
                      String location, String merchantName, String tags, boolean isRecurring,
                      String externalId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.transactionDate = transactionDate;
        this.userId = userId;
        this.categoryId = categoryId;
        this.notes = notes;
        this.location = location;
        this.merchantName = merchantName;
        this.tags = tags;
        this.isRecurring = isRecurring;
        this.externalId = externalId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        validate();
    }

    private void validate() {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
        if (description.length() > 255) {
            throw new IllegalArgumentException("Description cannot exceed 255 characters");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (amount.getAmount().signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (type == null) {
            throw new IllegalArgumentException("Transaction type cannot be null");
        }
        if (transactionDate == null) {
            throw new IllegalArgumentException("Transaction date cannot be null");
        }
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (transactionDate.isAfter(LocalDate.now().plusDays(1))) {
            throw new IllegalArgumentException("Transaction date cannot be in the future");
        }
    }

    public void update(String description, Money amount, TransactionType type,
                      LocalDate transactionDate, Long categoryId, String notes,
                      String location, String merchantName) {
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.transactionDate = transactionDate;
        this.categoryId = categoryId;
        this.notes = notes;
        this.location = location;
        this.merchantName = merchantName;
        this.updatedAt = LocalDateTime.now();
        validate();
    }

    public void categorize(Long categoryId) {
        this.categoryId = categoryId;
        this.updatedAt = LocalDateTime.now();
    }

    public void addTags(String tags) {
        this.tags = tags;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsRecurring(boolean isRecurring) {
        this.isRecurring = isRecurring;
        this.updatedAt = LocalDateTime.now();
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean belongsToUser(Long userId) {
        return this.userId.equals(userId);
    }

    public boolean isIncome() {
        return type == TransactionType.INCOME;
    }

    public boolean isExpense() {
        return type == TransactionType.EXPENSE;
    }

    public boolean isInDateRange(LocalDate startDate, LocalDate endDate) {
        return !transactionDate.isBefore(startDate) && !transactionDate.isAfter(endDate);
    }

    public boolean hasCategory(Long categoryId) {
        return this.categoryId != null && this.categoryId.equals(categoryId);
    }

    // Getters
    public Long getId() { return id; }
    public String getDescription() { return description; }
    public Money getAmount() { return amount; }
    public TransactionType getType() { return type; }
    public LocalDate getTransactionDate() { return transactionDate; }
    public Long getUserId() { return userId; }
    public Long getCategoryId() { return categoryId; }
    public String getNotes() { return notes; }
    public String getLocation() { return location; }
    public String getMerchantName() { return merchantName; }
    public String getTags() { return tags; }
    public boolean isRecurring() { return isRecurring; }
    public String getExternalId() { return externalId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", type=" + type +
                ", transactionDate=" + transactionDate +
                ", categoryId=" + categoryId +
                '}';
    }
}
