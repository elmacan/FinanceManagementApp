package com.example.FinanceManagementApp.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BudgetLimitRequest {
    @NotNull
    @Positive
    private BigDecimal newLimitAmount;

}
