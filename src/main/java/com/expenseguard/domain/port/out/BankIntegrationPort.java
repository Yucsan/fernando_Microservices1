// ============= BankIntegrationPort.java =============
package com.expenseguard.domain.port.out;

import com.expenseguard.domain.model.Transaction;
import java.time.LocalDate;
import java.util.List;

public interface BankIntegrationPort {
    
    List<BankTransaction> fetchTransactions(BankConnectionInfo connectionInfo, LocalDate fromDate, LocalDate toDate);
    boolean validateBankConnection(BankConnectionInfo connectionInfo);
    List<BankAccount> getUserBankAccounts(Long userId);
    
    record BankTransaction(
        String externalId,
        String description,
        String amount,
        String currency,
        LocalDate date,
        String category,
        String merchantName,
        String accountId
    ) {}
    
    record BankConnectionInfo(
        Long userId,
        String bankId,
        String accountId,
        String apiKey,
        String encryptedCredentials
    ) {}
    
    record BankAccount(
        String accountId,
        String accountName,
        String bankName,
        String accountType,
        String currency,
        boolean isActive
    ) {}
}