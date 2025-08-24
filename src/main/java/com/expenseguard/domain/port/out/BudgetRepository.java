// ============= BudgetRepository.java =============
package com.expenseguard.domain.port.out;

import com.expenseguard.domain.model.Budget;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BudgetRepository {
    
    Budget save(Budget budget);
    Optional<Budget> findById(Long id);
    Optional<Budget> findByIdAndUserId(Long id, Long userId);
    List<Budget> findByUserId(Long userId);
    List<Budget> findActiveBudgetsByUserId(Long userId);
    List<Budget> findBudgetsByUserIdAndCategoryId(Long userId, Long categoryId);
    List<Budget> findValidBudgetsForDate(Long userId, LocalDate date);
    void deleteById(Long id);
    boolean existsByIdAndUserId(Long id, Long userId);
}