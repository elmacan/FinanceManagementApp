package com.example.FinanceManagementApp.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SubscriptionRequest{
    @NotBlank
    private String name;

    @NotNull
    private Long categoryId;

    @NotNull
    @Positive
    private BigDecimal monthlyAmount;

    @NotNull
    private LocalDate startDate;

    @NotNull
    @Min(1) @Max(28)
    private Integer billingDay;

    @NotNull
    private Boolean active=true;



}
