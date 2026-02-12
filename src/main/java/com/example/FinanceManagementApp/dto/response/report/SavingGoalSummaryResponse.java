package com.example.FinanceManagementApp.dto.response.report;

import com.example.FinanceManagementApp.model.enums.CurrencyType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SavingGoalSummaryResponse {
    private Long goalId;
    private String title;

    private LocalDate targetDate;
    private Integer daysUntilTarget;
    private Boolean completed;


    private CurrencyType currency;

    private BigDecimal targetAmount;
    private BigDecimal savedAmount;
    private BigDecimal remainingAmount;

    private Integer progressPercent;

    private Integer entryCount;
}
