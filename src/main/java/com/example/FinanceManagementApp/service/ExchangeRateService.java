package com.example.FinanceManagementApp.service;

import com.example.FinanceManagementApp.currency.TcmbClient;
import com.example.FinanceManagementApp.model.entity.ExchangeRate;
import com.example.FinanceManagementApp.model.enums.CurrencyType;
import com.example.FinanceManagementApp.repository.ExchangeRateRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExchangeRateService {

    private final TcmbClient tcmbClient;
    private final ExchangeRateRepo repo;

    public Map<CurrencyType, BigDecimal> getTodayRates() {

        LocalDate today = LocalDate.now();

        Map<CurrencyType, BigDecimal> map = new EnumMap<>(CurrencyType.class);

        map.put(CurrencyType.TRY, BigDecimal.ONE);

        boolean missing = false;

        for (CurrencyType c : CurrencyType.values()) {

            if (c == CurrencyType.TRY )
                continue;

            var r = repo.findByCurrencyAndRateDate(c,today);

            if (r.isPresent()) {
                map.put(c, r.get().getTryRate());
            } else {
                missing = true;
            }
        }

        if (missing) fetchFromTcmb(today,map);

        return map;
    }

    private void fetchFromTcmb(
            LocalDate today,
            Map<CurrencyType,BigDecimal> map) {

        var resp = tcmbClient.getRates();

        resp.getCurrencies().forEach(c -> {

            if (c.getForexBuying()==null) return;

            try {
                CurrencyType type =
                        CurrencyType.valueOf(c.getCode());

                ExchangeRate e = repo.findByCurrencyAndRateDate(type,today).orElse(new ExchangeRate());

                e.setCurrency(type);
                e.setTryRate(c.getForexBuying());
                e.setRateDate(today);
                e.setSource("TCMB");

                repo.save(e);
                map.put(type,c.getForexBuying());

            } catch(Exception ignored){}
        });
    }
}