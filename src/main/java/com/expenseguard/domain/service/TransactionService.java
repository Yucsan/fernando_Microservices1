// ============= TransactionService.java =============
package com.expenseguard.domain.service;

import com.expenseguard.domain.model.Budget;
import com.expenseguard.domain.model.Category;
import com.expenseguard.domain.model.Transaction;
import com.expenseguard.domain.model.common.TransactionType;
import com.expenseguard.domain.port.in.TransactionUseCase;
import com.expenseguard.domain.port.out.*;
import com.expenseguard.shared.exception.BusinessException;
import com.expenseguard.shared.exception.NotFoundException;

import java.time.LocalDate;
import java.util.List;

public class TransactionService implements TransactionUseCase {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetRepository budgetRepository;
    private final NotificationPort notificationPort;

    public TransactionService(TransactionRepository transactionRepository,
                             CategoryRepository categoryRepository,
                             BudgetRepository budgetRepository,
                             NotificationPort notificationPort) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.budgetRepository = budgetRepository;
        this.notificationPort = notificationPort;
    }

    @Override
    public Transaction createTransaction(CreateTransactionCommand command) {
        // Validar que la categoría existe y pertenece al usuario
        if (command.categoryId() != null) {
            validateCategoryOwnership(command.categoryId(), command.userId());
        }

        Transaction transaction = new Transaction(
            command.description(),
            command.amount(),
            command.type(),
            command.transactionDate(),
            command.userId(),
            command.categoryId(),
            command.notes(),
            command.location(),
            command.merchantName()
        );

        Transaction savedTransaction = transactionRepository.save(transaction);

        // Actualizar presupuestos si es un gasto
        if (savedTransaction.getType() == TransactionType.EXPENSE) {
            updateBudgetsAfterExpense(savedTransaction);
        }

        return savedTransaction;
    }

    @Override
    public Transaction updateTransaction(UpdateTransactionCommand command) {
        Transaction existingTransaction = transactionRepository
            .findByIdAndUserId(command.transactionId(), command.userId())
            .orElseThrow(() -> new NotFoundException("Transaction not found"));

        // Validar que la categoría existe y pertenece al usuario
        if (command.categoryId() != null) {
            validateCategoryOwnership(command.categoryId(), command.userId());
        }

        // Revertir el impacto en presupuestos del monto anterior
        if (existingTransaction.getType() == TransactionType.EXPENSE) {
            revertBudgetImpact(existingTransaction);
        }

        // Actualizar la transacción
        existingTransaction.update(
            command.description(),
            command.amount(),
            command.type(),
            command.transactionDate(),
            command.categoryId(),
            command.notes(),
            command.location(),
            command.merchantName()
        );

        Transaction savedTransaction = transactionRepository.save(existingTransaction);

        // Aplicar el nuevo impacto en presupuestos si es un gasto
        if (savedTransaction.getType() == TransactionType.EXPENSE) {
            updateBudgetsAfterExpense(savedTransaction);
        }

        return savedTransaction;
    }

    @Override
    public void deleteTransaction(Long transactionId, Long userId) {
        Transaction transaction = transactionRepository
            .findByIdAndUserId(transactionId, userId)
            .orElseThrow(() -> new NotFoundException("Transaction not found"));

        // Revertir el impacto en presupuestos si es un gasto
        if (transaction.getType() == TransactionType.EXPENSE) {
            revertBudgetImpact(transaction);
        }

        transactionRepository.deleteById(transactionId);
    }

    @Override
    public Transaction getTransaction(Long transactionId, Long userId) {
        return transactionRepository
            .findByIdAndUserId(transactionId, userId)
            .orElseThrow(() -> new NotFoundException("Transaction not found"));
    }

    @Override
    public List<Transaction> getUserTransactions(Long userId, TransactionFilter filter) {
        return transactionRepository.findByUserId(userId, filter);
    }

    @Override
    public List<Transaction> getTransactionsByCategory(Long userId, Long categoryId, TransactionFilter filter) {
        validateCategoryOwnership(categoryId, userId);
        return transactionRepository.findByUserIdAndCategoryId(userId, categoryId, filter);
    }

    @Override
    public void categorizeTransaction(Long transactionId, Long categoryId, Long userId) {
        Transaction transaction = getTransaction(transactionId, userId);
        validateCategoryOwnership(categoryId, userId);
        
        transaction.categorize(categoryId);
        transactionRepository.save(transaction);
    }

    private void validateCategoryOwnership(Long categoryId, Long userId) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new NotFoundException("Category not found"));
        
        if (!category.isSystem() && !category.belongsToUser(userId)) {
            throw new BusinessException("Category does not belong to user");
        }
    }

    private void updateBudgetsAfterExpense(Transaction transaction) {
        List<Budget> affectedBudgets = budgetRepository
            .findValidBudgetsForDate(transaction.getUserId(), transaction.getTransactionDate());

        for (Budget budget : affectedBudgets) {
            // Actualizar presupuesto general o específico de categoría
            if (budget.isGeneralBudget() || budget.isForCategory(transaction.getCategoryId())) {
                budget.addExpense(transaction.getAmount());
                budgetRepository.save(budget);

                // Enviar notificaciones si es necesario
                checkBudgetAlerts(budget);
            }
        }
    }

    private void revertBudgetImpact(Transaction transaction) {
        List<Budget> affectedBudgets = budgetRepository
            .findValidBudgetsForDate(transaction.getUserId(), transaction.getTransactionDate());

        for (Budget budget : affectedBudgets) {
            if (budget.isGeneralBudget() || budget.isForCategory(transaction.getCategoryId())) {
                budget.removeExpense(transaction.getAmount());
                budgetRepository.save(budget);
            }
        }
    }

    private void checkBudgetAlerts(Budget budget) {
        if (budget.isAlertThresholdReached() && !budget.isExceeded()) {
            // Enviar alerta de presupuesto cerca del límite
            notificationPort.sendBudgetAlert(
                new NotificationPort.BudgetAlertNotification(
                    budget.getUserId(),
                    getUserEmail(budget.getUserId()),
                    budget.getName(),
                    budget.getSpentAmount().toString(),
                    budget.getBudgetAmount().toString(),
                    budget.getUsagePercentage()
                )
            );
        } else if (budget.isExceeded()) {
            // Enviar alerta de presupuesto excedido
            notificationPort.sendBudgetExceeded(
                new NotificationPort.BudgetExceededNotification(
                    budget.getUserId(),
                    getUserEmail(budget.getUserId()),
                    budget.getName(),
                    budget.getSpentAmount().toString(),
                    budget.getBudgetAmount().toString(),
                    budget.getSpentAmount().subtract(budget.getBudgetAmount()).toString()
                )
            );
        }
    }

    private String getUserEmail(Long userId) {
        // En una implementación real, obtendrías el email del repositorio de usuarios
        return "user@example.com";
    }
}