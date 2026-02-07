package com.example.FinanceManagementApp.dto.response;

import com.example.FinanceManagementApp.model.enums.CurrencyType;
import com.example.FinanceManagementApp.model.enums.TransactionSourceType;
import com.example.FinanceManagementApp.model.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private LocalDateTime transactionDate;
    private String description;

    private Long categoryId;
    private String categoryName;

    private TransactionSourceType sourceType;
    private Long sourceId;

    private String warning;
}
