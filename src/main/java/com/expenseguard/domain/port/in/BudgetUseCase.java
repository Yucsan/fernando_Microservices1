// ============= BudgetUseCase.java =============
package com.expenseguard.domain.port.in;

import com.expenseguard.domain.model.Budget;
import com.expenseguard.domain.model.common.DateRange;
import com.expenseguard.domain.model.common.Money;
import java.util.List;

public interface BudgetUseCase {
    
    Budget createBudget(CreateBudgetCommand command);
    Budget updateBudget(UpdateBudgetCommand command);
    void deleteBudget(Long budgetId, Long userId);
    Budget getBudget(Long budgetId, Long userId);
    List<Budget> getUserBudgets(Long userId);
    List<Budget> getActiveBudgets(Long userId);
    List<Budget> getBudgetsNearLimit(Long userId, double threshold);
    void recalculateBudgetSpent(Long budgetId);
    
    // Comandos
    record CreateBudgetCommand(
        String name,
        String description,
        Money budgetAmount,
        DateRange period,
        Long userId,
        Long categoryId,
        double alertThreshold
    ) {}
    
    record UpdateBudgetCommand(
        Long budgetId,
        String name,
        String description,
        Money budgetAmount,
        DateRange period,
        Long categoryId,
        double alertThreshold,
        Long userId
    ) {}
}