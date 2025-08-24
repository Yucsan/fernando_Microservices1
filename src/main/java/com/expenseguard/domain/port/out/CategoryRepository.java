// ============= CategoryRepository.java =============
package com.expenseguard.domain.port.out;

import com.expenseguard.domain.model.Category;
import com.expenseguard.domain.model.common.TransactionType;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    
    Category save(Category category);
    Optional<Category> findById(Long id);
    Optional<Category> findByIdAndUserId(Long id, Long userId);
    List<Category> findByUserId(Long userId);
    List<Category> findByUserIdAndType(Long userId, TransactionType type);
    List<Category> findSystemCategories();
    Optional<Category> findByNameAndUserId(String name, Long userId);
    void deleteById(Long id);
    boolean existsByIdAndUserId(Long id, Long userId);
    boolean existsByNameAndUserId(String name, Long userId);
}