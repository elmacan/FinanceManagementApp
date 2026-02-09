package com.example.FinanceManagementApp.dto.response;

import com.example.FinanceManagementApp.model.entity.Subscription;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SubscriptionResponse {

    private Long id;
    private String name;

    private Long categoryId;
    private String categoryName;

    private BigDecimal monthlyAmount;
    private LocalDate startDate;
    private Integer billingDay;

    private Boolean active;




    public SubscriptionResponse(Subscription s) {
        this.id = s.getId();
        this.name = s.getName();
        this.categoryId = s.getCategory().getId();
        this.categoryName = s.getCategory().getName();
        this.monthlyAmount = s.getMonthlyAmount();
        this.startDate = s.getStartDate();
        this.billingDay = s.getBillingDay();
        this.active = s.getActive();
    }


}
