
# 🏗️ Estructura del Proyecto ExpenseGuard

## 📁 Arquitectura Hexagonal - Layout Completo

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── expenseguard/
│   │           ├── ExpenseGuardApplication.java
│   │           │
│   │           ├── domain/                    # 🔵 NÚCLEO - Sin dependencias externas
│   │           │   ├── model/
│   │           │   │   ├── Transaction.java
│   │           │   │   ├── Category.java
│   │           │   │   ├── Budget.java
│   │           │   │   ├── User.java
│   │           │   │   └── common/
│   │           │   │       ├── Money.java
│   │           │   │       ├── DateRange.java
│   │           │   │       └── TransactionType.java
│   │           │   │
│   │           │   ├── service/               # Casos de uso
│   │           │   │   ├── TransactionService.java
│   │           │   │   ├── CategoryService.java
│   │           │   │   ├── BudgetService.java
│   │           │   │   └── ReportService.java
│   │           │   │
│   │           │   └── port/                  # Puertos (interfaces)
│   │           │       ├── in/                # Puertos primarios (driving)
│   │           │       │   ├── TransactionUseCase.java
│   │           │       │   ├── CategoryUseCase.java
│   │           │       │   └── BudgetUseCase.java
│   │           │       │
│   │           │       └── out/               # Puertos secundarios (driven)
│   │           │           ├── TransactionRepository.java
│   │           │           ├── CategoryRepository.java
│   │           │           ├── NotificationPort.java
│   │           │           ├── FileExportPort.java
│   │           │           └── BankIntegrationPort.java
│   │           │
│   │           ├── infrastructure/            # 🔴 ADAPTADORES
│   │           │   ├── adapter/
│   │           │   │   ├── in/                # Adaptadores primarios
│   │           │   │   │   ├── web/
│   │           │   │   │   │   ├── TransactionController.java
│   │           │   │   │   │   ├── CategoryController.java
│   │           │   │   │   │   ├── BudgetController.java
│   │           │   │   │   │   └── dto/
│   │           │   │   │   │       ├── request/
│   │           │   │   │   │       │   ├── CreateTransactionRequest.java
│   │           │   │   │   │       │   └── CreateBudgetRequest.java
│   │           │   │   │   │       └── response/
│   │           │   │   │   │           ├── TransactionResponse.java
│   │           │   │   │   │           └── BudgetResponse.java
│   │           │   │   │   │
│   │           │   │   │   └── cli/
│   │           │   │   │       └── CsvImportAdapter.java
│   │           │   │   │
│   │           │   │   └── out/               # Adaptadores secundarios
│   │           │   │       ├── persistence/
│   │           │   │       │   ├── jpa/
│   │           │   │       │   │   ├── entity/
│   │           │   │       │   │   │   ├── TransactionEntity.java
│   │           │   │       │   │   │   ├── CategoryEntity.java
│   │           │   │       │   │   │   └── BudgetEntity.java
│   │           │   │       │   │   │
│   │           │   │       │   │   ├── repository/
│   │           │   │       │   │   │   ├── JpaTransactionRepository.java
│   │           │   │       │   │   │   └── JpaCategoryRepository.java
│   │           │   │       │   │   │
│   │           │   │       │   │   └── adapter/
│   │           │   │       │   │       ├── TransactionJpaAdapter.java
│   │           │   │       │   │       └── CategoryJpaAdapter.java
│   │           │   │       │   │
│   │           │   │       │   └── file/
│   │           │   │       │       └── CsvExportAdapter.java
│   │           │   │       │
│   │           │   │       ├── external/
│   │           │   │       │   ├── BankApiAdapter.java
│   │           │   │       │   └── EmailNotificationAdapter.java
│   │           │   │       │
│   │           │   │       └── mapping/
│   │           │   │           ├── TransactionMapper.java
│   │           │   │           └── CategoryMapper.java
│   │           │   │
│   │           │   └── config/
│   │           │       ├── DatabaseConfig.java
│   │           │       ├── SecurityConfig.java
│   │           │       └── SwaggerConfig.java
│   │           │
│   │           └── shared/                    # 🟡 COMPARTIDO
│   │               ├── exception/
│   │               │   ├── GlobalExceptionHandler.java
│   │               │   ├── BusinessException.java
│   │               │   └── NotFoundException.java
│   │               │
│   │               └── validation/
│   │                   └── MoneyValidator.java
│   │
│   └── resources/
│       ├── application.yml
│       ├── application-dev.yml
│       ├── application-prod.yml
│       └── db/migration/
│           ├── V1__Create_users_table.sql
│           ├── V2__Create_categories_table.sql
│           ├── V3__Create_transactions_table.sql
│           └── V4__Create_budgets_table.sql
│
└── test/
    └── java/
        └── com/
            └── expenseguard/
                ├── domain/
                │   └── service/
                │       ├── TransactionServiceTest.java
                │       └── CategoryServiceTest.java
                │
                ├── infrastructure/
                │   ├── adapter/
                │   │   ├── in/
                │   │   │   └── web/
                │   │   │       └── TransactionControllerTest.java
                │   │   │
                │   │   └── out/
                │   │       └── persistence/
                │   │           └── TransactionJpaAdapterTest.java
                │   │
                │   └── integration/
                │       └── TransactionIntegrationTest.java
                │
                └── archunit/
                    └── HexagonalArchitectureTest.java
```

## 🎯 Principios de la Arquitectura

### 🔵 Dominio (Domain)
- **NO** depende de nada externo
- Contiene la lógica de negocio pura
- Define los puertos (interfaces)
- Es el centro de nuestra aplicación

### 🔴 Infraestructura (Infrastructure)
- **SÍ** depende del dominio
- Implementa los puertos del dominio
- Maneja detalles técnicos (DB, APIs, etc.)
- Se divide en adaptadores primarios y secundarios

### 🟡 Compartido (Shared)
- Utilidades transversales
- Excepciones globales
- Validaciones comunes

## 🚦 Flujo de Dependencias
```
Web Controller → Domain Service → Repository Port → JPA Adapter → Database
     ↑               ↑              ↑                ↑
   Primario      Casos de Uso    Puerto           Secundario
```

## 📋 Próximos Pasos
1. **Implementar entidades del dominio**
2. **Crear los puertos (interfaces)**
3. **Implementar casos de uso**
4. **Crear adaptadores de persistencia**
5. **Implementar API REST**
6. **Tests en cada capa**