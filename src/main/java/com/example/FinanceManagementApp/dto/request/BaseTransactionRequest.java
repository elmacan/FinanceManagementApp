package com.example.FinanceManagementApp.dto.request;

import com.example.FinanceManagementApp.model.enums.CurrencyType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public abstract class BaseTransactionRequest {
    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private CurrencyType currency;

    @NotNull
    private LocalDate transactionDate; // "2026-02-07"   7 şubat 2026

    private String description;

}
