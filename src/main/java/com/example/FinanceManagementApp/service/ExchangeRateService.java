package com.example.FinanceManagementApp.service;

import com.example.FinanceManagementApp.model.enums.CurrencyType;

import java.math.BigDecimal;
import java.util.Map;

public interface ExchangeRateService {
    Map<CurrencyType, BigDecimal> getTodayRates();
}
