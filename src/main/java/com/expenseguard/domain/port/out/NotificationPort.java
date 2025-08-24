// ============= NotificationPort.java =============
package com.expenseguard.domain.port.out;

public interface NotificationPort {
    
    void sendBudgetAlert(BudgetAlertNotification notification);
    void sendBudgetExceeded(BudgetExceededNotification notification);
    void sendMonthlyReport(MonthlyReportNotification notification);
    void sendTransactionAlert(TransactionAlertNotification notification);
    
    record BudgetAlertNotification(
        Long userId,
        String userEmail,
        String budgetName,
        String currentAmount,
        String budgetLimit,
        double usagePercentage
    ) {}
    
    record BudgetExceededNotification(
        Long userId,
        String userEmail,
        String budgetName,
        String currentAmount,
        String budgetLimit,
        String exceededAmount
    ) {}
    
    record MonthlyReportNotification(
        Long userId,
        String userEmail,
        String reportUrl,
        String totalExpenses,
        String totalIncome,
        String netAmount
    ) {}
    
    record TransactionAlertNotification(
        Long userId,
        String userEmail,
        String transactionDescription,
        String amount,
        String reason
    ) {}
}