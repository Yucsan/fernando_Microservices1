// ============= DatabaseConfig.java =============
package com.expenseguard.infrastructure.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.expenseguard.infrastructure.adapter.out.persistence.jpa.repository")
@EntityScan(basePackages = "com.expenseguard.infrastructure.adapter.out.persistence.jpa.entity")
@EnableJpaAuditing
@EnableTransactionManagement
public class DatabaseConfig {
    // Configuración automática por Spring Boot
}