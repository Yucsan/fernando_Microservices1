// ============= ReportUseCase.java =============
package com.expenseguard.domain.port.in;

import com.expenseguard.domain.model.common.DateRange;
import com.expenseguard.domain.model.common.Money;
import com.expenseguard.domain.model.common.TransactionType;
import java.util.List;
import java.util.Map;

public interface ReportUseCase {
    
    ExpenseReport generateExpenseReport(Long userId, DateRange period);
    IncomeReport generateIncomeReport(Long userId, DateRange period);
    CategoryReport generateCategoryReport(Long userId, DateRange period, TransactionType type);
    BudgetReport generateBudgetReport(Long userId, DateRange period);
    MonthlyTrendReport generateMonthlyTrends(Long userId, int months);
    
    // DTOs de respuesta
    record ExpenseReport(
        Money totalExpenses,
        Money averageDaily,
        Map<String, Money> expensesByCategory,
        List<TopExpense> topExpenses,
        DateRange period
    ) {}
    
    record IncomeReport(
        Money totalIncome,
        Money averageDaily,
        Map<String, Money> incomeByCategory,
        List<TopIncome> topIncomes,
        DateRange period
    ) {}
    
    record CategoryReport(
        String categoryName,
        Money total,
        int transactionCount,
        Money average,
        double percentageOfTotal,
        DateRange period
    ) {}
    
    record BudgetReport(
        List<BudgetSummary> budgetSummaries,
        Money totalBudgeted,
        Money totalSpent,
        double overallUsagePercentage,
        DateRange period
    ) {}
    
    record BudgetSummary(
        Long budgetId,
        String budgetName,
        Money budgeted,
        Money spent,
        Money remaining,
        double usagePercentage,
        boolean isExceeded,
        boolean isNearLimit
    ) {}
    
    record MonthlyTrendReport(
        List<MonthlyData> monthlyData,
        Money averageMonthlyIncome,
        Money averageMonthlyExpenses,
        double savingsRate
    ) {}
    
    record MonthlyData(
        String month,
        Money income,
        Money expenses,
        Money netAmount
    ) {}
    
    record TopExpense(
        String description,
        Money amount,
        String categoryName,
        String date
    ) {}
    
    record TopIncome(
        String description,
        Money amount,
        String categoryName,
        String date
    ) {}
}