package com.example.FinanceManagementApp.dto.response;

import com.example.FinanceManagementApp.model.entity.Budget;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@Data
public class BudgetResponse {
    private Long budgetId;
    private String scope;          // "TOTAL" veya "CATEGORY"
    private Long categoryId;       // total ise null
    private String categoryName;   // total ise null

    private Integer month;
    private Integer year;

    private BigDecimal limit;

    private BigDecimal actualSpent;   // Transaction sum
    private BigDecimal plannedSpent;  // PlannedExpense sum (completed=false)

    //TOPLAM = actual + planned
    private BigDecimal spentAmount;

    private BigDecimal remainingAmount;
    private Integer percentUsed;
    private boolean exceeded;

}