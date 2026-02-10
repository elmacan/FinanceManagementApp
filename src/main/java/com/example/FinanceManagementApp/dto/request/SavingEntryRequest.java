package com.example.FinanceManagementApp.dto.request;

import com.example.FinanceManagementApp.model.enums.CurrencyType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class SavingEntryRequest {
    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private CurrencyType currency;

}
