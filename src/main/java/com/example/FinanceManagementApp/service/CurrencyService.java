package com.example.FinanceManagementApp.service;

import com.example.FinanceManagementApp.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


import com.example.FinanceManagementApp.model.enums.CurrencyType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;
import java.util.Map;

@Service
public class CurrencyService {


     //TRY base rate table

    private static final Map<CurrencyType, BigDecimal> TRY_BASE_RATES = new EnumMap<>(CurrencyType.class);

    static {
        TRY_BASE_RATES.put(CurrencyType.TRY, BigDecimal.ONE);
        TRY_BASE_RATES.put(CurrencyType.USD, new BigDecimal("43.00"));
        TRY_BASE_RATES.put(CurrencyType.EUR, new BigDecimal("51.00"));
        TRY_BASE_RATES.put(CurrencyType.GOLD_GRAM, new BigDecimal("7230.00"));
    }


    public BigDecimal getRate(CurrencyType from, CurrencyType to) {

        validate(from, to);

        if (from == to) {
            return BigDecimal.ONE;
        }

        BigDecimal fromTry = TRY_BASE_RATES.get(from);
        BigDecimal toTry = TRY_BASE_RATES.get(to);

        if (fromTry == null || toTry == null) {
            throw new IllegalArgumentException("Currency rate not found");
        }

        // cross-rate hesap
        return fromTry.divide(toTry, 6, RoundingMode.HALF_UP);   //rate
    }

    public BigDecimal convert(BigDecimal amount, CurrencyType from, CurrencyType to) {

        if (amount == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST,"Amount cannot be null");
        }

        BigDecimal rate = getRate(from, to);

        return amount.multiply(rate).setScale(4, RoundingMode.HALF_UP);  //amount
    }


    private void validate(CurrencyType from, CurrencyType to) {
        if (from == null || to == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST,"Currency cannot be null");
        }
    }
}
