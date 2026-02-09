package com.example.FinanceManagementApp.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SubscriptionUpdateRequest {

    @NotBlank
    private String name;

    @NotNull
    private Long categoryId;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal monthlyAmount;

    @NotNull
    @Min(1) @Max(28)
    private Integer billingDay;
}
