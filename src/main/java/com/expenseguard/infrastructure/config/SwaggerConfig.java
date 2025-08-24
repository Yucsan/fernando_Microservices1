// ============= SwaggerConfig.java =============
package com.expenseguard.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.servlet.context-path:/}")
    private String contextPath;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .servers(List.of(
                new Server().url("http://localhost:8080" + contextPath).description("Development server"),
                new Server().url("https://api.expenseguard.com" + contextPath).description("Production server")
            ))
            .info(new Info()
                .title("ExpenseGuard API")
                .description("API para sistema de gestión de gastos personales con arquitectura hexagonal")
                .version("v1.0.0")
                .contact(new Contact()
                    .name("ExpenseGuard Team")
                    .email("dev@expenseguard.com")
                    .url("https://github.com/tu-usuario/expense-guard"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")));
    }
}