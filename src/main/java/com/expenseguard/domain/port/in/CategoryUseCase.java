// ============= CategoryUseCase.java =============
package com.expenseguard.domain.port.in;

import com.expenseguard.domain.model.Category;
import com.expenseguard.domain.model.common.TransactionType;
import java.util.List;

public interface CategoryUseCase {
    
    Category createCategory(CreateCategoryCommand command);
    Category updateCategory(UpdateCategoryCommand command);
    void deleteCategory(Long categoryId, Long userId);
    Category getCategory(Long categoryId, Long userId);
    List<Category> getUserCategories(Long userId);
    List<Category> getCategoriesByType(Long userId, TransactionType type);
    List<Category> getSystemCategories();
    Category suggestCategoryForTransaction(String description, String merchantName, Long userId);
    
    // Comandos
    record CreateCategoryCommand(
        String name,
        String description,
        String color,
        String icon,
        TransactionType type,
        Long userId
    ) {}
    
    record UpdateCategoryCommand(
        Long categoryId,
        String name,
        String description,
        String color,
        String icon,
        Long userId
    ) {}
}

