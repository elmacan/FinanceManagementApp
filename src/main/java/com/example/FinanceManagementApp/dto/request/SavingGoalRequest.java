package com.example.FinanceManagementApp.dto.request;

import com.example.FinanceManagementApp.model.entity.SavingGoal;
import com.example.FinanceManagementApp.model.enums.CurrencyType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SavingGoalRequest {
    @NotBlank
    private String title;

    @NotNull
    @Positive
    private BigDecimal targetAmount;

    @NotNull
    private CurrencyType currency;

    private LocalDate targetDate;

    public SavingGoalRequest(SavingGoal s) {
        this.title = s.getTitle();
        this.targetAmount = s.getTargetAmount();
        this.currency = s.getCurrency();
        this.targetDate = s.getTargetDate();
    }
}



//hedef :
//2000 USD
//50 gram GOLD
//100.000 TRY
