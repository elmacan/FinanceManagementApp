package com.example.FinanceManagementApp.dto.request;

import com.example.FinanceManagementApp.model.enums.CurrencyType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateUserRequest {

    @Size(min = 3, max = 50)
    private String userName;

    private CurrencyType baseCurrency;

    @Digits(integer = 15, fraction = 4)
    @PositiveOrZero
    private BigDecimal monthlyIncome;
}
