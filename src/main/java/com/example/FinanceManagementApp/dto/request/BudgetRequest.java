package com.example.FinanceManagementApp.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BudgetRequest {

    //if null -> total budget
    private Long categoryId;

    @NotNull
    @Positive
    private BigDecimal limitAmount;

    @NotNull
    @Min(1) @Max(12)
    private Integer month;

    @NotNull
    @Min(2000) @Max(2100)
    private Integer year;
}
