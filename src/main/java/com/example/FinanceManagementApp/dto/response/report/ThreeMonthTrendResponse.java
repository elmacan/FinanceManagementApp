package com.example.FinanceManagementApp.dto.response.report;

import com.example.FinanceManagementApp.model.enums.CurrencyType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThreeMonthTrendResponse {

    private CurrencyType currency;
    private String period;

    private List<MonthlyDataItem> monthlyData;

    private BigDecimal averageMonthlyIncome;
    private BigDecimal averageMonthlyExpense;
    private BigDecimal averageNetBalance;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyDataItem {
        private Integer month;
        private Integer year;
        private String monthName;
        private BigDecimal income;
        private BigDecimal expense;
        private BigDecimal netBalance;
        private TopCategory topExpenseCategory;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TopCategory {

        private Long categoryId;
        private String categoryName;
        private BigDecimal amount;
    }
}