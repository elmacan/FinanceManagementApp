package com.example.FinanceManagementApp.dto.response;

import com.example.FinanceManagementApp.model.entity.Transaction;
import com.example.FinanceManagementApp.model.enums.CurrencyType;
import com.example.FinanceManagementApp.model.enums.TransactionSourceType;
import com.example.FinanceManagementApp.model.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class TransactionResponse {


    private Long id;
    private BigDecimal originalAmount;
    private CurrencyType originalCurrency;

    private BigDecimal convertedAmount;
    private CurrencyType convertedCurrency;

    private BigDecimal rate;


    private TransactionType type;
    private LocalDate transactionDate;
    private String description;

    private Long categoryId;
    private String categoryName;

    private TransactionSourceType sourceType;
    private Long sourceId;

    private List<BudgetWarningResponse> budgetWarnings;

    public TransactionResponse(Transaction transaction, List<BudgetWarningResponse> budgetWarnings) {
        this.id = transaction.getId();
        this.originalAmount = transaction.getOriginalAmount();
        this.originalCurrency = transaction.getOriginalCurrency();
        this.convertedAmount = transaction.getConvertedAmount();
        this.convertedCurrency = transaction.getConvertedCurrency();
        this.rate = transaction.getRate();
        this.type = transaction.getType();
        this.transactionDate = transaction.getTransactionDate();
        this.description = transaction.getDescription();

        if (transaction.getCategory() != null) {
            this.categoryId = transaction.getCategory().getId();
            this.categoryName = transaction.getCategory().getName();
        }

        this.sourceType = transaction.getSourceType();
        this.sourceId = transaction.getSourceId();
        this.budgetWarnings = budgetWarnings;
    }
}
