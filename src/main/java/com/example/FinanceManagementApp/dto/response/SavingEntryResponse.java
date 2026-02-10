package com.example.FinanceManagementApp.dto.response;

import com.example.FinanceManagementApp.model.entity.SavingEntry;
import com.example.FinanceManagementApp.model.enums.CurrencyType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SavingEntryResponse {
    private Long id;

    private BigDecimal originalAmount;
    private CurrencyType originalCurrency;

    private BigDecimal convertedAmount;
    private CurrencyType goalCurrency;

    private BigDecimal rate;

    public SavingEntryResponse(SavingEntry e) {
        this.id = e.getId();
        this.originalAmount = e.getOriginalAmount();
        this.originalCurrency = e.getOriginalCurrency();
        this.convertedAmount = e.getConvertedAmount();
        this.goalCurrency = e.getGoalCurrency();
        this.rate = e.getRate();
    }
}
