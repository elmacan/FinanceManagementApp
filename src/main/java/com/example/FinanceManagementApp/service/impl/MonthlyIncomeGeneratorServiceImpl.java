package com.example.FinanceManagementApp.service.impl;

import com.example.FinanceManagementApp.model.entity.Transaction;
import com.example.FinanceManagementApp.model.entity.Users;
import com.example.FinanceManagementApp.model.enums.TransactionSourceType;
import com.example.FinanceManagementApp.model.enums.TransactionType;
import com.example.FinanceManagementApp.repository.TransactionRepo;
import com.example.FinanceManagementApp.service.MonthlyIncomeGeneratorService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MonthlyIncomeGeneratorServiceImpl implements MonthlyIncomeGeneratorService {

    private final TransactionRepo transactionRepo;

    @Transactional
    @Override
    public void ensureMonthlyIncome(Users user, int month, int year) {

        if (user.getMonthlyIncome() == null ||
                user.getMonthlyIncome().signum() == 0) {
            return;
        }

        boolean exists = transactionRepo.existsByUser_IdAndSourceTypeAndMonthAndYear(
                        user.getId(),
                        TransactionSourceType.MONTHLY_FIXED_INCOME,
                        month,
                        year
                );

        if (exists) return;

        Transaction tx = new Transaction();

        tx.setUser(user);
        tx.setType(TransactionType.INCOME);
        tx.setSourceType(TransactionSourceType.MONTHLY_FIXED_INCOME);

        tx.setOriginalAmount(user.getMonthlyIncome());
        tx.setOriginalCurrency(user.getBaseCurrency());

        tx.setConvertedAmount(user.getMonthlyIncome());
        tx.setConvertedCurrency(user.getBaseCurrency());
        tx.setRate(BigDecimal.ONE);

        tx.setTransactionDate(LocalDate.of(year, month, 1));
        tx.setDescription("Monthly fixed income");

        transactionRepo.save(tx);
    }

    @Transactional
    @Override
    public void ensureMonthlyIncomeForLastMonths(
            Users user,
            int monthsBack
    ) {
        LocalDate now = LocalDate.now();

        for (int i = 0; i < monthsBack; i++) {

            LocalDate d = now.minusMonths(i);

            ensureMonthlyIncome(
                    user,
                    d.getMonthValue(),
                    d.getYear()
            );
        }
    }


}
