// ============= BeanConfiguration.java =============
package com.expenseguard.infrastructure.config;

import com.expenseguard.domain.port.in.BudgetUseCase;
import com.expenseguard.domain.port.in.CategoryUseCase;
import com.expenseguard.domain.port.in.TransactionUseCase;
import com.expenseguard.domain.port.out.*;
import com.expenseguard.domain.service.BudgetService;
import com.expenseguard.domain.service.CategoryService;
import com.expenseguard.domain.service.TransactionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {
/*
    @Bean
    public TransactionUseCase transactionUseCase(
            TransactionRepository transactionRepository,
            CategoryRepository categoryRepository,
            BudgetRepository budgetRepository,
            NotificationPort notificationPort) {
        return new TransactionService(
            transactionRepository,
            categoryRepository,
            budgetRepository,
            notificationPort
        );
    }

    @Bean
    public CategoryUseCase categoryUseCase(
            CategoryRepository categoryRepository,
            TransactionRepository transactionRepository) {
        return new CategoryService(categoryRepository, transactionRepository);
    }

    @Bean
    public BudgetUseCase budgetUseCase(
            BudgetRepository budgetRepository,
            CategoryRepository categoryRepository,
            TransactionRepository transactionRepository) {
        return new BudgetService(budgetRepository, categoryRepository, transactionRepository);
    }
    */
}