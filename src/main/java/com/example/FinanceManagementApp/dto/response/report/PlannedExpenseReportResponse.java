package com.example.FinanceManagementApp.dto.response.report;

import com.example.FinanceManagementApp.model.entity.PlannedExpense;
import com.example.FinanceManagementApp.model.enums.CurrencyType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PlannedExpenseReportResponse {

    private Integer month;
    private Integer year;
    private CurrencyType currency;

    private BigDecimal totalPlannedAmount;
    private BigDecimal completedAmount;
    private BigDecimal pendingAmount;

    private Integer totalCount;
    private Integer completedCount;
    private Integer pendingCount;
    private Integer overdueCount;

    private List<CategorySummary> categories;
    private List<PlannedExpenseItem> items;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class CategorySummary {
        private Long categoryId;
        private String categoryName;
        private BigDecimal totalAmount;
        private Integer count;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class PlannedExpenseItem {
        private Long id;
        private String title;
        private String categoryName;
        private BigDecimal amount;
        private CurrencyType currency;
        private LocalDate plannedDate;
        private Boolean completed;
        private Boolean overdue;
    }
}
