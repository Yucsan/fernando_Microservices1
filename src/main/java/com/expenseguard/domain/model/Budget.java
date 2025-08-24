// ============= Budget.java - COMPLETA Y CORREGIDA =============
package com.expenseguard.domain.model;

import com.expenseguard.domain.model.common.DateRange;
import com.expenseguard.domain.model.common.Money;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class Budget {
    private final Long id;
    private String name;
    private String description;
    private Money budgetAmount;
    private Money spentAmount;
    private DateRange period;
    private final Long userId;
    private Long categoryId; // null = presupuesto general
    private BudgetStatus status;
    private double alertThreshold; // 0.0 a 1.0 (80% = 0.8)
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum BudgetStatus {
        ACTIVE("Active"),
        PAUSED("Paused"),
        EXCEEDED("Exceeded"),
        COMPLETED("Completed");

        private final String displayName;

        BudgetStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Constructor para creación
    public Budget(String name, String description, Money budgetAmount, DateRange period,
                  Long userId, Long categoryId, double alertThreshold) {
        this.id = null;
        this.name = name;
        this.description = description;
        this.budgetAmount = budgetAmount;
        this.spentAmount = new Money(0.0, budgetAmount.getCurrencyCode());
        this.period = period;
        this.userId = userId;
        this.categoryId = categoryId;
        this.status = BudgetStatus.ACTIVE;
        this.alertThreshold = alertThreshold;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        validate();
    }

    // Constructor completo para persistencia
    public Budget(Long id, String name, String description, Money budgetAmount, Money spentAmount,
                  DateRange period, Long userId, Long categoryId, BudgetStatus status,
                  double alertThreshold, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.budgetAmount = budgetAmount;
        this.spentAmount = spentAmount;
        this.period = period;
        this.userId = userId;
        this.categoryId = categoryId;
        this.status = status;
        this.alertThreshold = alertThreshold;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        validate();
    }

    private void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Budget name cannot be null or empty");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("Budget name cannot exceed 100 characters");
        }
        if (budgetAmount == null) {
            throw new IllegalArgumentException("Budget amount cannot be null");
        }
        if (budgetAmount.getAmount().signum() <= 0) {
            throw new IllegalArgumentException("Budget amount must be positive");
        }
        if (spentAmount == null) {
            throw new IllegalArgumentException("Spent amount cannot be null");
        }
        if (period == null) {
            throw new IllegalArgumentException("Budget period cannot be null");
        }
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (alertThreshold < 0.0 || alertThreshold > 1.0) {
            throw new IllegalArgumentException("Alert threshold must be between 0.0 and 1.0");
        }
        if (status == null) {
            throw new IllegalArgumentException("Budget status cannot be null");
        }
    }

    public void update(String name, String description, Money budgetAmount, DateRange period,
                      Long categoryId, double alertThreshold) {
        this.name = name;
        this.description = description;
        this.budgetAmount = budgetAmount;
        this.period = period;
        this.categoryId = categoryId;
        this.alertThreshold = alertThreshold;
        this.updatedAt = LocalDateTime.now();
        
        // Recalcular status si es necesario
        updateStatus();
        validate();
    }

    public void addExpense(Money expenseAmount) {
        if (!expenseAmount.getCurrency().equals(this.budgetAmount.getCurrency())) {
            throw new IllegalArgumentException("Currency mismatch in budget expense");
        }
        
        this.spentAmount = this.spentAmount.add(expenseAmount);
        this.updatedAt = LocalDateTime.now();
        updateStatus();
    }

    public void removeExpense(Money expenseAmount) {
        if (!expenseAmount.getCurrency().equals(this.budgetAmount.getCurrency())) {
            throw new IllegalArgumentException("Currency mismatch in budget expense");
        }
        
        this.spentAmount = this.spentAmount.subtract(expenseAmount);
        this.updatedAt = LocalDateTime.now();
        updateStatus();
    }

    public void recalculateSpentAmount(Money newSpentAmount) {
        if (!newSpentAmount.getCurrency().equals(this.budgetAmount.getCurrency())) {
            throw new IllegalArgumentException("Currency mismatch in budget recalculation");
        }
        
        this.spentAmount = newSpentAmount;
        this.updatedAt = LocalDateTime.now();
        updateStatus();
    }

    private void updateStatus() {
        if (status == BudgetStatus.PAUSED) {
            return; // No cambiar si está pausado manualmente
        }

        if (spentAmount.isGreaterThan(budgetAmount)) {
            this.status = BudgetStatus.EXCEEDED;
        } else if (period.getEndDate().isBefore(LocalDate.now())) {
            this.status = BudgetStatus.COMPLETED;
        } else {
            this.status = BudgetStatus.ACTIVE;
        }
    }

    public void pause() {
        this.status = BudgetStatus.PAUSED;
        this.updatedAt = LocalDateTime.now();
    }

    public void resume() {
        this.status = BudgetStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
        updateStatus(); // Recalcular el status correcto
    }

    public Money getRemainingAmount() {
        return budgetAmount.subtract(spentAmount);
    }

    public double getUsagePercentage() {
        if (budgetAmount.getAmount().signum() == 0) {
            return 0.0;
        }
        return spentAmount.getAmount().doubleValue() / budgetAmount.getAmount().doubleValue();
    }

    public boolean isAlertThresholdReached() {
        return getUsagePercentage() >= alertThreshold;
    }

    public boolean isExceeded() {
        return spentAmount.isGreaterThan(budgetAmount);
    }

    public boolean isActive() {
        return status == BudgetStatus.ACTIVE;
    }

    public boolean isForCategory(Long categoryId) {
        return this.categoryId != null && this.categoryId.equals(categoryId);
    }

    public boolean isGeneralBudget() {
        return this.categoryId == null;
    }

    public boolean belongsToUser(Long userId) {
        return this.userId.equals(userId);
    }

    public boolean isValidForDate(LocalDate date) {
        return period.contains(date);
    }

    public long getDaysRemaining() {
        LocalDate today = LocalDate.now();
        LocalDate endDate = period.getEndDate();
        
        if (endDate.isBefore(today)) {
            return 0;
        }
        
        return ChronoUnit.DAYS.between(today, endDate);
    }

    public double getDailyBudgetRemaining() {
        long daysRemaining = getDaysRemaining();
        if (daysRemaining <= 0) {
            return 0.0;
        }
        
        Money remainingAmount = getRemainingAmount();
        return remainingAmount.getAmount().doubleValue() / daysRemaining;
    }

    public boolean isNearExpiration(int warningDays) {
        return getDaysRemaining() <= warningDays && getDaysRemaining() > 0;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Money getBudgetAmount() { return budgetAmount; }
    public Money getSpentAmount() { return spentAmount; }
    public DateRange getPeriod() { return period; }
    public Long getUserId() { return userId; }
    public Long getCategoryId() { return categoryId; }
    public BudgetStatus getStatus() { return status; }
    public double getAlertThreshold() { return alertThreshold; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Budget budget = (Budget) o;
        return Objects.equals(id, budget.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Budget{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", budgetAmount=" + budgetAmount +
                ", spentAmount=" + spentAmount +
                ", period=" + period +
                ", status=" + status +
                ", usagePercentage=" + String.format("%.1f%%", getUsagePercentage() * 100) +
                ", daysRemaining=" + getDaysRemaining() +
                '}';
    }
}