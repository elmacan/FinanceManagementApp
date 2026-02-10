package com.example.FinanceManagementApp.dto.response;

import com.example.FinanceManagementApp.model.enums.CurrencyType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class SavingGoalProgressResponse {

    private Long goalId;
    private String title;

    private BigDecimal targetAmount;
    private CurrencyType currency;
    private LocalDate targetDate;

    private BigDecimal currentAmount;   // goal.currency
    private BigDecimal remainingAmount; // goal.currency

    private Integer percent;
    private Boolean completed;
}
