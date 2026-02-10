package com.example.FinanceManagementApp.dto.response;

import com.example.FinanceManagementApp.model.entity.Budget;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BudgetResponse {
    private Long budgetId;
    private String scope;          // "TOTAL" veya "CATEGORY"
    private Long categoryId;       // total ise null
    private String categoryName;   // total ise null

    private Integer month;
    private Integer year;

    private BigDecimal budgetLimit;

    private BigDecimal actualExpenseAmount;
    private BigDecimal plannedExpenseAmount;
    private BigDecimal totalExpenseWithPlan;

    private BigDecimal remainingBudgetNow;
    private BigDecimal remainingBudgetWithPlan;

    private Integer budgetUsagePercentNow;
    private Integer budgetUsagePercentWithPlan;

    private Boolean exceededNow;
    private Boolean exceededWithPlan;

}