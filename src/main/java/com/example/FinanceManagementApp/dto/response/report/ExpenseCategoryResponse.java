package com.example.FinanceManagementApp.dto.response.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseCategoryResponse {

    private Integer month;
    private Integer year;
    private BigDecimal totalExpense;
    private List<CategoryItem> categories;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryItem {
        private Long categoryId;
        private String categoryName;
        private BigDecimal totalAmount;
        private Integer transactionCount;
    }
}