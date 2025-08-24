package com.expenseguard.domain.port.out;

import com.expenseguard.domain.model.Transaction;
import com.expenseguard.domain.port.in.TransactionUseCase.TransactionFilter;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository {
    
    Transaction save(Transaction transaction);
    Optional<Transaction> findById(Long id);
    Optional<Transaction> findByIdAndUserId(Long id, Long userId);
    List<Transaction> findByUserId(Long userId, TransactionFilter filter);
    List<Transaction> findByUserIdAndCategoryId(Long userId, Long categoryId, TransactionFilter filter);
    List<Transaction> findByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate);
    List<Transaction> findByUserIdAndCategoryIdAndDateRange(Long userId, Long categoryId, LocalDate startDate, LocalDate endDate);
    void deleteById(Long id);
    boolean existsByIdAndUserId(Long id, Long userId);
    long countByUserId(Long userId);
    long countByUserIdAndCategoryId(Long userId, Long categoryId);
}