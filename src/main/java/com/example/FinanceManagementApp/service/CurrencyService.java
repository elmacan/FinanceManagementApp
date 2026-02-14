package com.example.FinanceManagementApp.service;

import com.example.FinanceManagementApp.model.enums.CurrencyType;
import jakarta.annotation.PostConstruct;

import java.math.BigDecimal;

public interface CurrencyService {
    @PostConstruct
    void init();

    BigDecimal getRate(CurrencyType from, CurrencyType to);

    BigDecimal convert(BigDecimal amount, CurrencyType from, CurrencyType to);
}
