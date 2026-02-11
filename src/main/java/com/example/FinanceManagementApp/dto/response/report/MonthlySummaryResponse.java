package com.example.FinanceManagementApp.dto.response.report;

import com.example.FinanceManagementApp.model.enums.CurrencyType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlySummaryResponse {
    private Integer month;
    private Integer year;
    private CurrencyType currency;

    private BigDecimal fixedMonthlyIncome;
    private BigDecimal transactionIncome;

    private BigDecimal totalIncome;
    private BigDecimal totalExpense;

    private BigDecimal netBalance;
    private BigDecimal savingRate;  //(net / income) * 100

}
