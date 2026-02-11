package com.example.FinanceManagementApp.repository;

import com.example.FinanceManagementApp.model.entity.ExchangeRate;
import com.example.FinanceManagementApp.model.enums.CurrencyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ExchangeRateRepo extends JpaRepository<ExchangeRate,Long> {

    Optional<ExchangeRate> findByCurrencyAndRateDate(CurrencyType currency, LocalDate rateDate);
}