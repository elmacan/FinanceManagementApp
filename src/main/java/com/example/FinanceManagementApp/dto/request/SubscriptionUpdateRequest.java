package com.example.FinanceManagementApp.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SubscriptionUpdateRequest {


    private String name;


    private Long categoryId;

    @Positive
    private BigDecimal monthlyAmount;


    @Min(1) @Max(28)
    private Integer billingDay;
}
