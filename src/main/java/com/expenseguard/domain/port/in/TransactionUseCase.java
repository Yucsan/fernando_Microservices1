// ============= TransactionUseCase.java =============
package com.expenseguard.domain.port.in;

import com.expenseguard.domain.model.Transaction;
import com.expenseguard.domain.model.common.Money;
import com.expenseguard.domain.model.common.TransactionType;
import java.time.LocalDate;
import java.util.List;

public interface TransactionUseCase {
    
    Transaction createTransaction(CreateTransactionCommand command);
    Transaction updateTransaction(UpdateTransactionCommand command);
    void deleteTransaction(Long transactionId, Long userId);
    Transaction getTransaction(Long transactionId, Long userId);
    List<Transaction> getUserTransactions(Long userId, TransactionFilter filter);
    List<Transaction> getTransactionsByCategory(Long userId, Long categoryId, TransactionFilter filter);
    void categorizeTransaction(Long transactionId, Long categoryId, Long userId);
    
    // Comandos
    record CreateTransactionCommand(
        String description,
        Money amount,
        TransactionType type,
        LocalDate transactionDate,
        Long userId,
        Long categoryId,
        String notes,
        String location,
        String merchantName
    ) {}
    
    record UpdateTransactionCommand(
        Long transactionId,
        String description,
        Money amount,
        TransactionType type,
        LocalDate transactionDate,
        Long categoryId,
        String notes,
        String location,
        String merchantName,
        Long userId
    ) {}
    
    record TransactionFilter(
        LocalDate startDate,
        LocalDate endDate,
        TransactionType type,
        Long categoryId,
        String searchText,
        int page,
        int size
    ) {
        public static TransactionFilter empty() {
            return new TransactionFilter(null, null, null, null, null, 0, 50);
        }
    }
}