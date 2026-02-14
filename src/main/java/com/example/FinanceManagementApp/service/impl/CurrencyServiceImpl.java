package com.example.FinanceManagementApp.service.impl;

import com.example.FinanceManagementApp.service.CurrencyService;
import com.example.FinanceManagementApp.exception.ApiException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


import com.example.FinanceManagementApp.model.enums.CurrencyType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class CurrencyServiceImpl implements CurrencyService {
    private final ExchangeRateServiceImpl rateService;
    private Map<CurrencyType, BigDecimal> rates;


    @PostConstruct
    @Override
    public void init() {
        rates = rateService.getTodayRates();
    }
    private void refreshIfNeeded() {
        rates = rateService.getTodayRates();
    }



    @Override
    public BigDecimal getRate(CurrencyType from, CurrencyType to) {

        validate(from, to);

        if (from == to) {return BigDecimal.ONE;}

        BigDecimal fromTry = rates.get(from);
        BigDecimal toTry = rates.get(to);

        if (fromTry == null || toTry == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST,"Currency rate not found");
        }

        // cross-rate hesap
        return fromTry.divide(toTry, 6, RoundingMode.HALF_UP);   //rate
    }

    @Override
    public BigDecimal convert(BigDecimal amount, CurrencyType from, CurrencyType to) {

        if (amount == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST,"Amount cannot be null");
        }

        refreshIfNeeded();

        return amount.multiply(getRate(from,to)).setScale(4, RoundingMode.HALF_UP);  //amount
    }


    private void validate(CurrencyType from, CurrencyType to) {
        if (from == null || to == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST,"Currency cannot be null");
        }
    }
}
