package com.example.FinanceManagementApp.service;

import com.example.FinanceManagementApp.dto.response.report.ExpenseCategoryResponse;
import com.example.FinanceManagementApp.dto.response.report.MonthlySummaryResponse;
import com.example.FinanceManagementApp.model.entity.Users;
import com.example.FinanceManagementApp.model.enums.TransactionType;
import com.example.FinanceManagementApp.repository.TransactionRepo;
import com.example.FinanceManagementApp.security.CurrentUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final TransactionRepo transactionRepo;
    private final MonthlyIncomeGeneratorService incomeGenerator;




    public MonthlySummaryResponse monthlySummary(CurrentUserPrincipal p, int month, int year) {
        Users user=p.getUser();
        incomeGenerator.ensureMonthlyIncome(user, month, year);

        List<Object[]> rows = transactionRepo.sumIncomeExpenseByMonth(user.getId(), month, year);


        BigDecimal income = BigDecimal.ZERO;
        BigDecimal expense = BigDecimal.ZERO;

        for (Object[] r : rows) {

            TransactionType type = (TransactionType) r[0];
            BigDecimal sum = (BigDecimal) r[1];

            if (type == TransactionType.INCOME) {
                income = sum;
            }

            if (type == TransactionType.EXPENSE) {
                expense = sum;
            }
        }

        BigDecimal net = income.subtract(expense);
        BigDecimal savingRate = BigDecimal.ZERO;

        if (income.compareTo(BigDecimal.ZERO) > 0) {
            savingRate = net.multiply(BigDecimal.valueOf(100)).divide(income, 2, RoundingMode.HALF_UP);
        }

        BigDecimal fixedIncome = transactionRepo.sumFixedIncome(user.getId(), month, year);

        BigDecimal transactionIncome = income.subtract(fixedIncome);



        MonthlySummaryResponse r = new MonthlySummaryResponse();

        r.setMonth(month);
        r.setYear(year);
        r.setCurrency(p.getUser().getBaseCurrency());

        r.setFixedMonthlyIncome(fixedIncome);
        r.setTransactionIncome(transactionIncome);

        r.setTotalIncome(income);
        r.setTotalExpense(expense);
        r.setNetBalance(net);
        r.setSavingRate(savingRate);

        return r;
    }


    public ExpenseCategoryResponse buildExpenseCategoryReport(CurrentUserPrincipal p, Integer month, Integer year) {
        List<Object[]> rows = transactionRepo.expenseCategoryDistribution(p.getId(), month, year);


        List<ExpenseCategoryResponse.CategoryItem> items = rows.stream()
                .map(r -> new ExpenseCategoryResponse.CategoryItem(
                        (Long) r[0],
                        (String) r[1],
                        (BigDecimal) r[2],
                        ((Long) r[3]).intValue()
                ))
                .toList();

        BigDecimal totalExpense =
                transactionRepo.totalExpenseForMonth(
                        p.getId(),
                        month,
                        year
                );
        if (totalExpense == null) {
            totalExpense = BigDecimal.ZERO;
        }
        return new ExpenseCategoryResponse(
                month,
                year,
                totalExpense,
                items
        );
    }
}
