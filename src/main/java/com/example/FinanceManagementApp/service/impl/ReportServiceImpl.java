package com.example.FinanceManagementApp.service.impl;

import com.example.FinanceManagementApp.dto.response.BudgetResponse;
import com.example.FinanceManagementApp.dto.response.report.*;
import com.example.FinanceManagementApp.exception.ApiException;
import com.example.FinanceManagementApp.model.entity.*;
import com.example.FinanceManagementApp.model.enums.BillStatus;
import com.example.FinanceManagementApp.model.enums.CurrencyType;
import com.example.FinanceManagementApp.model.enums.TransactionSourceType;
import com.example.FinanceManagementApp.model.enums.TransactionType;
import com.example.FinanceManagementApp.repository.*;
import com.example.FinanceManagementApp.security.CurrentUserPrincipal;
import com.example.FinanceManagementApp.service.BudgetService;
import com.example.FinanceManagementApp.service.ReportService;
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
public class ReportServiceImpl implements ReportService {
    private final TransactionRepo transactionRepo;
    private final MonthlyIncomeGeneratorServiceImpl incomeGenerator;
    private final BudgetService budgetService;
    private final BillRepo billRepo;
    private final SubscriptionRepo subscriptionRepo;
    private final SavingGoalRepo savingGoalRepo;
    private final SavingEntryRepo savingEntryRepo;
    private final PlannedExpenseRepo plannedExpenseRepo;




    @Override
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


    @Override
    public ExpenseCategoryResponse buildExpenseCategoryReport(CurrentUserPrincipal p, int month, int year) {
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

    @Override
    public ThreeMonthTrendResponse threeMonthTrend(CurrentUserPrincipal principal) {

        Users user = principal.getUser();
        LocalDate now = LocalDate.now();

        LocalDate start = LocalDate.now().minusMonths(2);
        int startKey = start.getYear() * 100 + start.getMonthValue();
        int endKey= now.getYear() * 100 + now.getMonthValue();

        List<Object[]> raw = transactionRepo.threeMonthTrendRaw(
                user.getId(),
                startKey,
                endKey
        );

        List<ThreeMonthTrendResponse.MonthlyDataItem> months = new ArrayList<>();

        for (Object[] r : raw) {

            int year = (Integer) r[0];
            int month = (Integer) r[1];
            TransactionType type = (TransactionType) r[2];
            BigDecimal amount = (BigDecimal) r[3];

            ThreeMonthTrendResponse.MonthlyDataItem m = findOrCreate(months, year, month);

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


        List<Object[]> catRaw  = transactionRepo.categoryExpenseFor3Months(
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

            ThreeMonthTrendResponse.TopCategory current = topMap.get(key);

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


        BigDecimal avgIncome = avg(months.stream().map(ThreeMonthTrendResponse.MonthlyDataItem::getIncome).toList());

        BigDecimal avgExpense =
                avg(months.stream().map(ThreeMonthTrendResponse.MonthlyDataItem::getExpense).toList());

        BigDecimal avgNet =
                avg(months.stream().map(ThreeMonthTrendResponse.MonthlyDataItem::getNetBalance).toList());

        if (months.isEmpty()) {
            return new ThreeMonthTrendResponse(
                    user.getBaseCurrency(),
                    "No Data",
                    List.of(),
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO
            );
        }

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

    private ThreeMonthTrendResponse.MonthlyDataItem findOrCreate(
            List<ThreeMonthTrendResponse.MonthlyDataItem> list,
            int year,
            int month
    ) {
        for (ThreeMonthTrendResponse.MonthlyDataItem m : list) {
            if (m.getYear()==year && m.getMonth()==month)
                return m;
        }

        ThreeMonthTrendResponse.MonthlyDataItem m  = new ThreeMonthTrendResponse.MonthlyDataItem(
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


    @Override
    public List<BudgetResponse> buildBudgetReport(Integer month, Integer year, CurrentUserPrincipal p) {
        return budgetService.list(month, year, p);
    }

    @Override
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


    @Override
    public SubscriptionReportResponse buildSubscriptionReport(CurrentUserPrincipal p) {
        Users user = p.getUser();

        int month = LocalDate.now().getMonthValue();
        int year  = LocalDate.now().getYear();

        List<Subscription> subs = subscriptionRepo.findByUser_Id(user.getId());

        List<SubscriptionReportResponse.SubscriptionItem> items = new ArrayList<>();

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
                    new SubscriptionReportResponse.SubscriptionItem(
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

    @Override
    public SavingGoalWithEntityResponse buildSavingGoalReport(CurrentUserPrincipal principal, Long goalId) {
        Users user = principal.getUser();

        SavingGoal goal = savingGoalRepo
                .findByIdAndUser_Id(goalId, user.getId())
                .orElseThrow(() ->
                        new ApiException(HttpStatus.NOT_FOUND, "Saving goal not found"));

        List<SavingEntry> entries = savingEntryRepo.findByGoal_IdAndGoal_User_IdOrderByDateAsc(goalId, principal.getId());


        BigDecimal[] savedAndRemaining=savingGoalStats(goal, principal.getId());

        int percent = 0;

        if (goal.getTargetAmount().signum() > 0) {
            percent = savedAndRemaining[0]
                    .multiply(BigDecimal.valueOf(100))
                    .divide(goal.getTargetAmount(), 0, RoundingMode.HALF_UP)
                    .intValue();
        }


        List<SavingGoalWithEntityResponse.EntryItem> entryItems = new ArrayList<>();

        for (SavingEntry e : entries) {
            entryItems.add(
                    new SavingGoalWithEntityResponse.EntryItem(
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

        return new SavingGoalWithEntityResponse(
                goal.getId(),
                goal.getTitle(),
                goal.getTargetDate(),
                countDaysUntil(goal),
                goal.getCompleted(),
                goal.getCurrency(),
                goal.getTargetAmount(),
                savedAndRemaining[0],
                savedAndRemaining[1],
                percent,
                entryItems.size(),
                entryItems
        );


    }


    @Override
    public List<SavingGoalSummaryResponse> allSavingGoalsReport(CurrentUserPrincipal principal) {
        Users user = principal.getUser();
        Long userId = principal.getId();

        List<SavingGoal> goals = savingGoalRepo.findByUser_Id(userId);

        List<SavingGoalSummaryResponse> results = new ArrayList<>();

        for (SavingGoal goal : goals) {

            BigDecimal[] savedAndRemaining=savingGoalStats(goal,userId);

            int percent = 0;

            if (goal.getTargetAmount().signum() > 0) {
                percent = savedAndRemaining[0]
                        .multiply(BigDecimal.valueOf(100))
                        .divide(goal.getTargetAmount(), 0, RoundingMode.HALF_UP)
                        .intValue();
            }

            Integer entryCount = savingEntryRepo.countByGoal_IdAndGoal_User_Id(goal.getId(), userId);

            results.add(
                    new SavingGoalSummaryResponse(
                            goal.getId(),
                            goal.getTitle(),
                            goal.getTargetDate(),
                            countDaysUntil(goal),
                            goal.getCompleted(),
                            goal.getCurrency(),
                            goal.getTargetAmount(),
                            savedAndRemaining[0],
                            savedAndRemaining[1],
                            percent,
                            entryCount
                    )
            );
        }

        return results;




    }

   private BigDecimal[] savingGoalStats (SavingGoal goal,Long userId) {
            //[0] -> savedAmount
            //[1] -> remaining

       BigDecimal savedAmount = savingEntryRepo.sumProgress(goal.getId(),userId);
       if (savedAmount == null) savedAmount = BigDecimal.ZERO;

       BigDecimal remaining = goal.getTargetAmount().subtract(savedAmount);

       if (remaining.signum() < 0) remaining = BigDecimal.ZERO;

       return new BigDecimal[]{savedAmount, remaining};

   }

   private Integer countDaysUntil(SavingGoal goal) {
       Integer daysUntil = null;

       if (goal.getTargetDate() != null) {
           daysUntil = (int) ChronoUnit.DAYS.between(
                   LocalDate.now(),
                   goal.getTargetDate()
           );
       }
       return daysUntil;
   }

    @Override
    public PlannedExpenseReportResponse buildPlannedReport(CurrentUserPrincipal principal, int month, int year) {
        Users user = principal.getUser();
        CurrencyType currency = user.getBaseCurrency();
        LocalDate today = LocalDate.now();

        List<PlannedExpense> list = plannedExpenseRepo.findForReport(user.getId(), month, year);

        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal completedAmount = BigDecimal.ZERO;

        int completedCount = 0;
        int overdueCount = 0;

        Map<Long, BigDecimal> catAmountMap = new HashMap<>();
        Map<Long, Integer> catCountMap = new HashMap<>();
        Map<Long, String> catNameMap = new HashMap<>();

        List<PlannedExpenseReportResponse.PlannedExpenseItem> items = new ArrayList<>();

        for (PlannedExpense p : list) {

            BigDecimal amt = p.getConvertedAmount();
            totalAmount = totalAmount.add(amt);

            boolean completed = Boolean.TRUE.equals(p.getCompleted());
            boolean overdue = !completed && p.getPlannedDate().isBefore(today);

            if (completed) {
                completedAmount = completedAmount.add(amt);
                completedCount++;
            }

            if (overdue) {
                overdueCount++;
            }
            Long catId = p.getCategory().getId();
            String catName = p.getCategory().getName();

            catNameMap.put(catId, catName);

            catAmountMap.put(catId, catAmountMap.getOrDefault(catId, BigDecimal.ZERO).add(amt));

            catCountMap.put(catId, catCountMap.getOrDefault(catId, 0) + 1);

            items.add(
                    new PlannedExpenseReportResponse.PlannedExpenseItem(
                            p.getId(),
                            p.getTitle(),
                            catName,
                            amt,
                            p.getConvertedCurrency(),
                            p.getPlannedDate(),
                            completed,
                            overdue
                    )
            );
        }

        BigDecimal pendingAmount = totalAmount.subtract(completedAmount);
        int totalCount = list.size();
        int pendingCount = totalCount - completedCount;

        List<PlannedExpenseReportResponse.CategorySummary> categories = new ArrayList<>();

        for (Long catId : catAmountMap.keySet()) {
            categories.add(new PlannedExpenseReportResponse.CategorySummary(
                            catId,
                            catNameMap.get(catId),
                            catAmountMap.get(catId),
                            catCountMap.get(catId)
                    )
            );
        }
        return new PlannedExpenseReportResponse(
                month,
                year,
                currency,

                totalAmount,
                completedAmount,
                pendingAmount,

                totalCount,
                completedCount,
                pendingCount,
                overdueCount,

                categories,
                items
        );
    }



}
