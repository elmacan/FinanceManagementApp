package com.example.FinanceManagementApp.dto.response.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SubscriptionReportResponse {
    private BigDecimal monthlyTotalCost;
    private BigDecimal notChargedYetCost;

    private List<SubscriptionItem> subscriptions;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubscriptionItem {

        private Long subscriptionId;
        private String name;
        private Long categoryId;
        private String categoryName;
        private BigDecimal monthlyAmount;
        private Integer billingDay;
        private Boolean active;
        private Boolean chargedThisMonth;
    }
}
