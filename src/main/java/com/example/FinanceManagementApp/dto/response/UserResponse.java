package com.example.FinanceManagementApp.dto.response;

import com.example.FinanceManagementApp.model.enums.CurrencyType;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@AllArgsConstructor
@Data
public class UserResponse {

    private Long id;
    private String email;
    private String userName;
    private CurrencyType baseCurrency;
    private BigDecimal monthlyIncome;



}
