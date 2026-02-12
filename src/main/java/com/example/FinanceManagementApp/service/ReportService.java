package com.example.FinanceManagementApp.service;

import com.example.FinanceManagementApp.dto.response.BudgetResponse;
import com.example.FinanceManagementApp.dto.response.report.*;
import com.example.FinanceManagementApp.exception.ApiException;
import com.example.FinanceManagementApp.model.entity.*;
import com.example.FinanceManagementApp.model.enums.BillStatus;
import com.example.FinanceManagementApp.model.enums.TransactionSourceType;
import com.example.FinanceManagementApp.model.enums.TransactionType;
import com.example.FinanceManagementApp.repository.*;
import com.example.FinanceManagementApp.security.CurrentUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final TransactionRepo transactionRepo;
    private final MonthlyIncomeGeneratorService incomeGenerator;
    private final BudgetService budgetService;
    private final BillRepo billRepo;
    private final SubscriptionRepo subscriptionRepo;
    private final SavingGoalRepo savingGoalRepo;
    private final SavingEntryRepo savingEntryRepo;




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

    public ThreeMonthTrendResponse threeMonthTrend(CurrentUserPrincipal principal) {

        Users user = principal.getUser();
        LocalDate now = LocalDate.now();

        LocalDate start = LocalDate.now().minusMonths(2);
        int startKey = start.getYear() * 100 + start.getMonthValue();
        int endKey= now.getYear() * 100 + now.getMonthValue();

        var raw = transactionRepo.threeMonthTrendRaw(
                user.getId(),
                startKey,
                endKey
        );

        List<ThreeMonthTrendResponse.MonthlyData> months = new ArrayList<>();

        for (Object[] r : raw) {

            int year = (Integer) r[0];
            int month = (Integer) r[1];
            TransactionType type = (TransactionType) r[2];
            BigDecimal amount = (BigDecimal) r[3];

            var m = findOrCreate(months, year, month);

            if (type == TransactionType.INCOME)
                m.setIncome(amount);
            else
                m.setExpense(amount);
        }


        months.forEach(m ->
                m.setNetBalance(
                        m.getIncome().subtract(m.getExpense())
                )
        );


        var catRaw = transactionRepo.categoryExpenseFor3Months(
                user.getId(),
                startKey,
                endKey
        );

        Map<String, ThreeMonthTrendResponse.TopCategory> topMap = new HashMap<>();

        for (Object[] r : catRaw) {

            int year = (Integer) r[0];
            int month = (Integer) r[1];
            Long id = (Long) r[2];
            String name = (String) r[3];
            BigDecimal amount = (BigDecimal) r[4];

            String key = year + "-" + month;

            var current = topMap.get(key);

            if (current == null || amount.compareTo(current.getAmount()) > 0) {
                topMap.put(key,
                        new ThreeMonthTrendResponse.TopCategory(
                                id, name, amount));
            }
        }

        months.forEach(m ->
                m.setTopExpenseCategory(
                        topMap.get(m.getYear() + "-" + m.getMonth())
                )
        );


        BigDecimal avgIncome = avg(months.stream().map(ThreeMonthTrendResponse.MonthlyData::getIncome).toList());

        BigDecimal avgExpense =
                avg(months.stream().map(ThreeMonthTrendResponse.MonthlyData::getExpense).toList());

        BigDecimal avgNet =
                avg(months.stream().map(ThreeMonthTrendResponse.MonthlyData::getNetBalance).toList());

        return new ThreeMonthTrendResponse(
                user.getBaseCurrency(),
                months.get(0).getMonthName() + " - " +
                        months.get(months.size() - 1).getMonthName(),
                months,
                avgIncome,
                avgExpense,
                avgNet
        );
    }

    private BigDecimal avg(List<BigDecimal> list) {

        if (list.isEmpty()) return BigDecimal.ZERO;

        BigDecimal sum = BigDecimal.ZERO;

        for (BigDecimal b : list)
            sum = sum.add(b);

        return sum.divide(
                BigDecimal.valueOf(list.size()),
                2,
                RoundingMode.HALF_UP
        );
    }

    private String monthName(int m) {
        return Month.of(m).getDisplayName(
                        TextStyle.FULL,
                        new Locale("tr")
                );
    }

    private ThreeMonthTrendResponse.MonthlyData findOrCreate(
            List<ThreeMonthTrendResponse.MonthlyData> list,
            int year,
            int month
    ) {
        for (var m : list) {
            if (m.getYear()==year && m.getMonth()==month)
                return m;
        }

        var m = new ThreeMonthTrendResponse.MonthlyData(
                month,
                year,
                monthName(month)+" "+year,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                null
        );

        list.add(m);
        return m;
    }


    public List<BudgetResponse> buildBudgetReport(Integer month, Integer year, CurrentUserPrincipal p) {
        return budgetService.list(month, year, p);
    }

    public BillReportResponse buildBillReport(CurrentUserPrincipal p) {
        Long userId = p.getId();

        List<Bill> all = billRepo.findByUser_Id(userId);

        int paid = 0;
        int unpaid = 0;
        int overdue = 0;

        List<BillReportResponse.BillItem> unpaidList = new ArrayList<>();
        List<BillReportResponse.BillItem> paidList = new ArrayList<>();
        List<BillReportResponse.BillItem> overDueList = new ArrayList<>();


        LocalDate today = LocalDate.now();

        for (Bill b : all) {

            boolean isOverdue = b.getStatus() != BillStatus.PAID && b.getDueDate() != null && b.getDueDate().isBefore(today);

            if (b.getStatus() == BillStatus.PAID) {
                paid++;
                paidList.add(new BillReportResponse.BillItem(
                        b.getId(),
                        b.getName(),
                        b.getAmount(),
                        b.getDueDate(),
                        b.getStatus()
                ));
            } else if (isOverdue) {
                overdue++;
                overDueList.add(new BillReportResponse.BillItem(
                        b.getId(),
                        b.getName(),
                        b.getAmount(),
                        b.getDueDate(),
                        BillStatus.OVERDUE
                ));

            } else {
                unpaid++;
                unpaidList.add(new BillReportResponse.BillItem(
                        b.getId(),
                        b.getName(),
                        b.getAmount(),
                        b.getDueDate(),
                        b.getStatus()
                ));
            }


        }

            BigDecimal totalUnpaid = billRepo.totalUnpaidAmount(userId);

            BillReportResponse r = new BillReportResponse();

            r.setTotalBills(all.size());
            r.setPaidCount(paid);
            r.setUnpaidCount(unpaid);
            r.setOverdueCount(overdue);
            r.setTotalUnpaidAmount(totalUnpaid);
            r.setUnpaidBills(unpaidList);
            r.setOverDueBills(overDueList);
            r.setPaidBills(paidList);

            return r;

    }


    public SubscriptionReportResponse buildSubscriptionReport(CurrentUserPrincipal p) {
        Users user = p.getUser();

        int month = LocalDate.now().getMonthValue();
        int year  = LocalDate.now().getYear();

        List<Subscription> subs = subscriptionRepo.findByUser_Id(user.getId());

        List<SubscriptionReportResponse.Item> items = new ArrayList<>();

        BigDecimal total = BigDecimal.ZERO;
        BigDecimal notChargedTotal = BigDecimal.ZERO;



        for (Subscription s : subs) {

            boolean charged = transactionRepo
                            .existsByUser_IdAndSourceTypeAndSourceIdAndMonthAndYear(
                                    user.getId(),
                                    TransactionSourceType.SUBSCRIPTION,
                                    s.getId(),
                                    month,
                                    year
                            );

            if (Boolean.TRUE.equals(s.getActive())) {
                total = total.add(s.getMonthlyAmount());
                if (!charged) {
                    notChargedTotal =
                            notChargedTotal.add(s.getMonthlyAmount());
                }
            }

            items.add(
                    new SubscriptionReportResponse.Item(
                            s.getId(),
                            s.getName(),
                            s.getCategory().getId(),
                            s.getCategory().getName(),
                            s.getMonthlyAmount(),
                            s.getBillingDay(),
                            s.getActive(),
                            charged
                    )
            );
        }

        SubscriptionReportResponse r = new SubscriptionReportResponse();
        r.setMonthlyTotalCost(total);
        r.setNotChargedYetCost(notChargedTotal);
        r.setSubscriptions(items);

        return r;
    }

    public SavingGoalReportResponse buildSavingGoalReport(CurrentUserPrincipal principal,Long goalId) {
        Users user = principal.getUser();

        SavingGoal goal = savingGoalRepo
                .findByIdAndUser_Id(goalId, user.getId())
                .orElseThrow(() ->
                        new ApiException(HttpStatus.NOT_FOUND, "Saving goal not found"));

        List<SavingEntry> entries = savingEntryRepo.findByGoal_IdOrderByDateAsc(goalId);


        BigDecimal savedAmount = savingEntryRepo.sumConvertedForGoal(goalId);

        if (savedAmount == null)
            savedAmount = BigDecimal.ZERO;


        BigDecimal remaining = goal.getTargetAmount().subtract(savedAmount);

        if (remaining.signum() < 0)
            remaining = BigDecimal.ZERO;

        int percent = 0;

        if (goal.getTargetAmount().signum() > 0) {
            percent = savedAmount
                    .multiply(BigDecimal.valueOf(100))
                    .divide(goal.getTargetAmount(), 0, RoundingMode.HALF_UP)
                    .intValue();
        }


        Integer daysUntil = null;

        if (goal.getTargetDate() != null) {
            daysUntil = (int) ChronoUnit.DAYS.between(
                    LocalDate.now(),
                    goal.getTargetDate()
            );
        }


        List<SavingGoalReportResponse.EntryItem> entryItems = new ArrayList<>();

        for (SavingEntry e : entries) {
            entryItems.add(
                    new SavingGoalReportResponse.EntryItem(
                            e.getId(),
                            e.getOriginalAmount(),
                            e.getOriginalCurrency(),
                            e.getConvertedAmount(),
                            e.getGoalCurrency(),
                            e.getRate(),
                            e.getDate()
                    )
            );
        }

        return new SavingGoalReportResponse(
                goal.getId(),
                goal.getTitle(),
                goal.getTargetDate(),
                daysUntil,
                goal.getCompleted(),

                goal.getCurrency(),

                goal.getTargetAmount(),
                savedAmount,
                remaining,

                percent,
                entryItems.size(),

                entryItems
        );


    }
}
