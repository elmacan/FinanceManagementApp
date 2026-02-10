package com.example.FinanceManagementApp.dto.response;

import com.example.FinanceManagementApp.model.entity.PlannedExpense;
import com.example.FinanceManagementApp.model.enums.CurrencyType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PlannedExpenseResponse {

    private Long id;
    private String title;
    private Long categoryId;
    private String categoryName;

    private BigDecimal amount;
    private CurrencyType currency;

    private BigDecimal convertedAmount;
    private CurrencyType convertedCurrency;
    private BigDecimal rate;

    private LocalDate plannedDate;
    private String description;
    private Boolean completed;
    private LocalDateTime createdAt;

    public PlannedExpenseResponse(PlannedExpense plannedExpense) {
        this.id = plannedExpense.getId();
        this.title = plannedExpense.getTitle();
        this.categoryId = plannedExpense.getCategory().getId();
        this.categoryName = plannedExpense.getCategory().getName();
        this.amount=plannedExpense.getOriginalAmount();
        this.currency=plannedExpense.getCurrency();
        this.convertedAmount=plannedExpense.getConvertedAmount();
        this.convertedCurrency=plannedExpense.getConvertedCurrency();
        this.rate=plannedExpense.getRate();
        this.plannedDate=plannedExpense.getPlannedDate();
        this.description=plannedExpense.getDescription();
        this.completed=plannedExpense.getCompleted();
        this.createdAt=plannedExpense.getCreatedAt();

    }



}
