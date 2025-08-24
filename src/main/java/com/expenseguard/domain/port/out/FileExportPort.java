package com.expenseguard.domain.port.out;

import com.expenseguard.domain.model.Transaction;
import java.util.List;

public interface FileExportPort {
    
    byte[] exportTransactionsToCsv(List<Transaction> transactions);
    byte[] exportTransactionsToPdf(List<Transaction> transactions, String title);
    byte[] exportBudgetReportToPdf(Long userId, String reportData);
    
    record ExportResult(
        byte[] fileContent,
        String fileName,
        String contentType
    ) {}
}