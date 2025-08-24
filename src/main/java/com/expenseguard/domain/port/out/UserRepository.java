// ============= UserRepository.java =============
package com.expenseguard.domain.port.out;

import com.expenseguard.domain.model.User;
import java.util.Optional;

public interface UserRepository {
    
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    void deleteById(Long id);
    boolean existsByEmail(String email);
}