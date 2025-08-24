// ============= BudgetService.java =============
package com.expenseguard.domain.service;

import com.expenseguard.domain.model.Budget;
import com.expenseguard.domain.model.Transaction;
import com.expenseguard.domain.model.common.Money;
import com.expenseguard.domain.model.common.TransactionType;
import com.expenseguard.domain.port.in.BudgetUseCase;
import com.expenseguard.domain.port.out.BudgetRepository;
import com.expenseguard.domain.port.out.CategoryRepository;
import com.expenseguard.domain.port.out.TransactionRepository;
import com.expenseguard.shared.exception.BusinessException;
import com.expenseguard.shared.exception.NotFoundException;

import java.math.BigDecimal;
import java.util.List;

public class BudgetService implements BudgetUseCase {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    public BudgetService(BudgetRepository budgetRepository,
                        CategoryRepository categoryRepository,
                        TransactionRepository transactionRepository) {
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Budget createBudget(CreateBudgetCommand command) {
        // Validar que la categoría existe si se especifica
        if (command.categoryId() != null) {
            validateCategoryOwnership(command.categoryId(), command.userId());
        }

        // Verificar que no existe un presupuesto activo para la misma categoría y período
        List<Budget> existingBudgets = budgetRepository.findBudgetsByUserIdAndCategoryId(
            command.userId(), command.categoryId());
        
        boolean hasOverlappingBudget = existingBudgets.stream()
            .anyMatch(budget -> budget.isActive() && hasDateOverlap(budget, command));
        
        if (hasOverlappingBudget) {
            throw new BusinessException("Active budget already exists for this category and period");
        }

        Budget budget = new Budget(
            command.name(),
            command.description(),
            command.budgetAmount(),
            command.period(),
            command.userId(),
            command.categoryId(),
            command.alertThreshold()
        );

        Budget savedBudget = budgetRepository.save(budget);
        
        // Calcular el gasto actual para el período
        recalculateBudgetSpent(savedBudget.getId());
        
        return budgetRepository.findById(savedBudget.getId()).orElse(savedBudget);
    }

    @Override
    public Budget updateBudget(UpdateBudgetCommand command) {
        Budget budget = budgetRepository
            .findByIdAndUserId(command.budgetId(), command.userId())
            .orElseThrow(() -> new NotFoundException("Budget not found"));

        // Validar que la categoría existe si se especifica
        if (command.categoryId() != null) {
            validateCategoryOwnership(command.categoryId(), command.userId());
        }

        budget.update(
            command.name(),
            command.description(),
            command.budgetAmount(),
            command.period(),
            command.categoryId(),
            command.alertThreshold()
        );

        Budget savedBudget = budgetRepository.save(budget);
        
        // Recalcular el gasto después de la actualización
        recalculateBudgetSpent(savedBudget.getId());
        
        return budgetRepository.findById(savedBudget.getId()).orElse(savedBudget);
    }

    @Override
    public void deleteBudget(Long budgetId, Long userId) {
        Budget budget = budgetRepository
            .findByIdAndUserId(budgetId, userId)
            .orElseThrow(() -> new NotFoundException("Budget not found"));

        budgetRepository.deleteById(budgetId);
    }

    @Override
    public Budget getBudget(Long budgetId, Long userId) {
        return budgetRepository
            .findByIdAndUserId(budgetId, userId)
            .orElseThrow(() -> new NotFoundException("Budget not found"));
    }

    @Override
    public List<Budget> getUserBudgets(Long userId) {
        return budgetRepository.findByUserId(userId);
    }

    @Override
    public List<Budget> getActiveBudgets(Long userId) {
        return budgetRepository.findActiveBudgetsByUserId(userId);
    }

    @Override
    public List<Budget> getBudgetsNearLimit(Long userId, double threshold) {
        return budgetRepository.findActiveBudgetsByUserId(userId).stream()
            .filter(budget -> budget.getUsagePercentage() >= threshold)
            .toList();
    }

    @Override
    public void recalculateBudgetSpent(Long budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
            .orElseThrow(() -> new NotFoundException("Budget not found"));

        // Obtener todas las transacciones de gastos en el período del presupuesto
        List<Transaction> transactions = transactionRepository
            .findByUserIdAndDateRange(
                budget.getUserId(),
                budget.getPeriod().getStartDate(),
                budget.getPeriod().getEndDate()
            ).stream()
            .filter(t -> t.getType() == TransactionType.EXPENSE)
            .filter(t -> budget.isGeneralBudget() || t.hasCategory(budget.getCategoryId()))
            .toList();

        // Calcular el total gastado
        Money totalSpent = transactions.stream()
            .map(Transaction::getAmount)
            .filter(amount -> amount.getCurrency().equals(budget.getBudgetAmount().getCurrency()))
            .reduce(
                new Money(BigDecimal.ZERO, budget.getBudgetAmount().getCurrency()),
                Money::add
            );

        // Crear un nuevo presupuesto con el monto actualizado
        Budget updatedBudget = new Budget(
            budget.getId(),
            budget.getName(),
            budget.getDescription(),
            budget.getBudgetAmount(),
            totalSpent, // Nuevo monto gastado calculado
            budget.getPeriod(),
            budget.getUserId(),
            budget.getCategoryId(),
            budget.getStatus(),
            budget.getAlertThreshold(),
            budget.getCreatedAt(),
            budget.getUpdatedAt()
        );

        budgetRepository.save(updatedBudget);
    }

    private void validateCategoryOwnership(Long categoryId, Long userId) {
        var category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new NotFoundException("Category not found"));
        
        if (!category.isSystem() && !category.belongsToUser(userId)) {
            throw new BusinessException("Category does not belong to user");
        }
    }

    private boolean hasDateOverlap(Budget existingBudget, CreateBudgetCommand command) {
        return !(command.period().getEndDate().isBefore(existingBudget.getPeriod().getStartDate()) ||
                command.period().getStartDate().isAfter(existingBudget.getPeriod().getEndDate()));
    }
}