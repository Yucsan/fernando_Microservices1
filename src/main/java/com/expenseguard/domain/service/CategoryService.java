// ============= CategoryService.java =============
package com.expenseguard.domain.service;

import com.expenseguard.domain.model.Category;
import com.expenseguard.domain.model.Transaction;
import com.expenseguard.domain.model.common.TransactionType;
import com.expenseguard.domain.port.in.CategoryUseCase;
import com.expenseguard.domain.port.in.TransactionUseCase;
import com.expenseguard.domain.port.out.CategoryRepository;
import com.expenseguard.domain.port.out.TransactionRepository;
import com.expenseguard.shared.exception.BusinessException;
import com.expenseguard.shared.exception.NotFoundException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CategoryService implements CategoryUseCase {

    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final CategorySuggestionEngine suggestionEngine;

    public CategoryService(CategoryRepository categoryRepository,
                          TransactionRepository transactionRepository) {
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
        this.suggestionEngine = new CategorySuggestionEngine();
    }

    @Override
    public Category createCategory(CreateCategoryCommand command) {
        // Validar que no existe una categoría con el mismo nombre
        if (categoryRepository.existsByNameAndUserId(command.name(), command.userId())) {
            throw new BusinessException("Category with this name already exists");
        }

        Category category = new Category(
            command.name(),
            command.description(),
            command.color(),
            command.icon(),
            command.type(),
            command.userId(),
            false // No es una categoría del sistema
        );

        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(UpdateCategoryCommand command) {
        Category category = categoryRepository
            .findByIdAndUserId(command.categoryId(), command.userId())
            .orElseThrow(() -> new NotFoundException("Category not found"));

        // Validar que no existe otra categoría con el mismo nombre
        Category existingWithSameName = categoryRepository
            .findByNameAndUserId(command.name(), command.userId())
            .orElse(null);
        
        if (existingWithSameName != null && !existingWithSameName.getId().equals(command.categoryId())) {
            throw new BusinessException("Category with this name already exists");
        }

        category.update(command.name(), command.description(), command.color(), command.icon());
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long categoryId, Long userId) {
        Category category = categoryRepository
            .findByIdAndUserId(categoryId, userId)
            .orElseThrow(() -> new NotFoundException("Category not found"));

        if (category.isSystem()) {
            throw new BusinessException("Cannot delete system categories");
        }

        // Verificar si hay transacciones asociadas
        long transactionCount = transactionRepository.countByUserIdAndCategoryId(userId, categoryId);
        if (transactionCount > 0) {
            throw new BusinessException("Cannot delete category with associated transactions");
        }

        categoryRepository.deleteById(categoryId);
    }

    @Override
    public Category getCategory(Long categoryId, Long userId) {
        return categoryRepository
            .findByIdAndUserId(categoryId, userId)
            .orElseThrow(() -> new NotFoundException("Category not found"));
    }

    @Override
    public List<Category> getUserCategories(Long userId) {
        return categoryRepository.findByUserId(userId);
    }

    @Override
    public List<Category> getCategoriesByType(Long userId, TransactionType type) {
        return categoryRepository.findByUserIdAndType(userId, type);
    }

    @Override
    public List<Category> getSystemCategories() {
        return categoryRepository.findSystemCategories();
    }

    @Override
    public Category suggestCategoryForTransaction(String description, String merchantName, Long userId) {
        // Obtener transacciones pasadas del usuario para aprender patrones
        List<Transaction> userTransactions = transactionRepository
            .findByUserId(userId, TransactionUseCase.TransactionFilter.empty());
        
        // Obtener categorías disponibles
        List<Category> availableCategories = getUserCategories(userId);
        availableCategories.addAll(getSystemCategories());

        return suggestionEngine.suggestCategory(description, merchantName, userTransactions, availableCategories);
    }

    // Clase interna para sugerencias inteligentes de categorías
    private static class CategorySuggestionEngine {
        private static final Map<String, String> MERCHANT_PATTERNS = Map.of(
            "supermarket", "Groceries",
            "gas", "Transportation",
            "restaurant", "Dining",
            "pharmacy", "Healthcare",
            "cinema", "Entertainment",
            "uber", "Transportation",
            "netflix", "Entertainment",
            "amazon", "Shopping"
        );

        private static final Map<String, String> DESCRIPTION_PATTERNS = Map.of(
            "fuel", "Transportation",
            "grocery", "Groceries",
            "coffee", "Dining",
            "lunch", "Dining",
            "dinner", "Dining",
            "medicine", "Healthcare",
            "doctor", "Healthcare",
            "movie", "Entertainment",
            "concert", "Entertainment"
        );

        public Category suggestCategory(String description, String merchantName,
                                      List<Transaction> userTransactions, List<Category> availableCategories) {
            
            // 1. Intentar por coincidencias de merchant
            Category merchantMatch = findCategoryByMerchant(merchantName, availableCategories);
            if (merchantMatch != null) {
                return merchantMatch;
            }

            // 2. Intentar por patrones en la descripción
            Category descriptionMatch = findCategoryByDescription(description, availableCategories);
            if (descriptionMatch != null) {
                return descriptionMatch;
            }

            // 3. Aprender de transacciones pasadas similares
            Category historicalMatch = findCategoryByHistory(description, merchantName, userTransactions, availableCategories);
            if (historicalMatch != null) {
                return historicalMatch;
            }

            // 4. Retornar categoría por defecto
            return availableCategories.stream()
                .filter(cat -> cat.getName().equalsIgnoreCase("Other") || cat.getName().equalsIgnoreCase("Miscellaneous"))
                .findFirst()
                .orElse(availableCategories.get(0));
        }

        private Category findCategoryByMerchant(String merchantName, List<Category> categories) {
            if (merchantName == null) return null;
            
            String merchant = merchantName.toLowerCase();
            for (Map.Entry<String, String> pattern : MERCHANT_PATTERNS.entrySet()) {
                if (merchant.contains(pattern.getKey())) {
                    return categories.stream()
                        .filter(cat -> cat.getName().equalsIgnoreCase(pattern.getValue()))
                        .findFirst()
                        .orElse(null);
                }
            }
            return null;
        }

        private Category findCategoryByDescription(String description, List<Category> categories) {
            if (description == null) return null;
            
            String desc = description.toLowerCase();
            for (Map.Entry<String, String> pattern : DESCRIPTION_PATTERNS.entrySet()) {
                if (desc.contains(pattern.getKey())) {
                    return categories.stream()
                        .filter(cat -> cat.getName().equalsIgnoreCase(pattern.getValue()))
                        .findFirst()
                        .orElse(null);
                }
            }
            return null;
        }

        private Category findCategoryByHistory(String description, String merchantName, 
                                             List<Transaction> transactions, List<Category> categories) {
            // Buscar transacciones similares basadas en descripción o merchant
            Map<Long, Long> categoryFrequency = transactions.stream()
                .filter(t -> t.getCategoryId() != null)
                .filter(t -> isSimilarTransaction(t, description, merchantName))
                .collect(Collectors.groupingBy(
                    Transaction::getCategoryId,
                    Collectors.counting()
                ));

            if (categoryFrequency.isEmpty()) {
                return null;
            }

            // Encontrar la categoría más frecuente
            Long mostFrequentCategoryId = categoryFrequency.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

            return categories.stream()
                .filter(cat -> cat.getId().equals(mostFrequentCategoryId))
                .findFirst()
                .orElse(null);
        }

        private boolean isSimilarTransaction(Transaction transaction, String description, String merchantName) {
            if (description != null && transaction.getDescription() != null) {
                String[] descWords = description.toLowerCase().split("\\s+");
                String transDesc = transaction.getDescription().toLowerCase();
                for (String word : descWords) {
                    if (word.length() > 3 && transDesc.contains(word)) {
                        return true;
                    }
                }
            }

            if (merchantName != null && transaction.getMerchantName() != null) {
                return transaction.getMerchantName().toLowerCase().contains(merchantName.toLowerCase()) ||
                       merchantName.toLowerCase().contains(transaction.getMerchantName().toLowerCase());
            }

            return false;
        }
    }
}