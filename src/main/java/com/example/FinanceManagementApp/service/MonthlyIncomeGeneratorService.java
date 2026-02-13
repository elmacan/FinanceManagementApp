package com.example.FinanceManagementApp.service;

import com.example.FinanceManagementApp.model.entity.Users;
import jakarta.transaction.Transactional;

public interface MonthlyIncomeGeneratorService {
    @Transactional
    void ensureMonthlyIncome(Users user, int month, int year);

    @Transactional
    void ensureMonthlyIncomeForLastMonths(
            Users user,
            int monthsBack
    );
}
