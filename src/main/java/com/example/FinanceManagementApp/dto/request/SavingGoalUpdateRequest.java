package com.example.FinanceManagementApp.dto.request;

import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SavingGoalUpdateRequest {
    private String title;

    @Positive
    private BigDecimal targetAmount;

    private LocalDate targetDate;
}
