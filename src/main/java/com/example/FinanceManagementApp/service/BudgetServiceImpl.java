package com.example.FinanceManagementApp.service;

import com.example.FinanceManagementApp.dto.request.BudgetRequest;
import com.example.FinanceManagementApp.dto.response.BudgetResponse;
import com.example.FinanceManagementApp.dto.response.BudgetWarningResponse;
import com.example.FinanceManagementApp.exception.ApiException;
import com.example.FinanceManagementApp.model.entity.Budget;
import com.example.FinanceManagementApp.model.entity.Category;
import com.example.FinanceManagementApp.model.entity.Users;
import com.example.FinanceManagementApp.model.enums.TransactionType;
import com.example.FinanceManagementApp.model.enums.WarningLevel;
import com.example.FinanceManagementApp.repository.BudgetRepo;
import com.example.FinanceManagementApp.repository.CategoryRepo;
import com.example.FinanceManagementApp.repository.PlannedExpenseRepo;
import com.example.FinanceManagementApp.repository.TransactionRepo;
import com.example.FinanceManagementApp.security.CurrentUserPrincipal;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepo budgetRepo;
    private final CategoryRepo categoryRepo;
    private final TransactionRepo transactionRepo;
    private final PlannedExpenseRepo plannedExpenseRepo;




    @Transactional
    @Override
    public BudgetResponse create(@Valid BudgetRequest dto, CurrentUserPrincipal principal) {
        Users user=principal.getUser();

        //nullsa total budget
        Category category=null;

        if (dto.getCategoryId() == null) {

            if (budgetRepo.existsByUserAndCategoryIsNullAndMonthAndYear(
                    user, dto.getMonth(), dto.getYear())) {

                throw new ApiException(
                        HttpStatus.CONFLICT,
                        "Total budget already exists for this month/year"
                );
            }
        }else{

            category = categoryRepo
                    .findByIdAndUser_Id(dto.getCategoryId(), user.getId())
                    .orElseThrow(() -> new ApiException(
                            HttpStatus.NOT_FOUND, "Category not found"));

            if (category.getType() != TransactionType.EXPENSE) {
                throw new ApiException(
                        HttpStatus.BAD_REQUEST,
                        "Budget only for EXPENSE categories");
            }

            if (budgetRepo.existsByUserAndCategoryAndMonthAndYear(
                    user, category, dto.getMonth(), dto.getYear())) {
                throw new ApiException(
                        HttpStatus.CONFLICT,
                        "Budget already exists for this category/month/year");
            }
        }

        Budget b=new Budget();
        b.setUser(user);
        b.setLimitAmount(dto.getLimitAmount());
        b.setMonth(dto.getMonth());
        b.setYear(dto.getYear());
        b.setCategory(category);

        Budget saved=budgetRepo.save(b);

        return toResponse(saved,user);
    }

    @Override
    public BudgetResponse get(Long id, CurrentUserPrincipal principal) {
        Long userId = principal.getId();

        Budget b = budgetRepo.findByIdAndUser_Id(id, userId)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND, "Budget not found"));

        return toResponse(b, b.getUser());
    }



    @Override
    public List<BudgetResponse> list(Integer month, Integer year, CurrentUserPrincipal principal) {
        Users user=principal.getUser();

        if ((month == null) != (year == null)) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "month and year must be provided together"
            );
        }

        List<Budget> list = (month != null)
                ? budgetRepo.findByUserAndMonthAndYear(user, month, year)
                : budgetRepo.findByUser(user);

        return list.stream()
                .map(b -> toResponse(b, user))
                .toList();
    }


    @Transactional
    @Override
    public BudgetResponse updateLimit(CurrentUserPrincipal principal, Long budgetId, BigDecimal newLimitAmount) {

        if (newLimitAmount == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "limitAmount is required");
        }

        Long userId = principal.getId();

        Budget budget = budgetRepo.findByIdAndUser_Id(budgetId, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Budget not found"));

        budget.setLimitAmount(newLimitAmount);

        return toResponse(budgetRepo.save(budget), budget.getUser());

    }

    private BudgetResponse toResponse(Budget b, Users user) {

        BigDecimal actualSpent = transactionRepo.sumExpenseForBudget(
                user,
                b.getCategory(),
                b.getMonth(),
                b.getYear()
        );
        if (actualSpent == null) {
            actualSpent = BigDecimal.ZERO;
        }

        BigDecimal plannedSpent = plannedExpenseRepo.sumPlannedForBudget(
                user,
                b.getCategory(),
                b.getMonth(),
                b.getYear()
        );
        if (plannedSpent == null) {
            plannedSpent = BigDecimal.ZERO;
        }

        BigDecimal limit = b.getLimitAmount();

        BigDecimal totalWithPlan = actualSpent.add(plannedSpent);

        BigDecimal remainingNow = limit.subtract(actualSpent);
        BigDecimal remainingWithPlan = limit.subtract(totalWithPlan);

        int percentNow = percent(actualSpent, limit);
        int percentWithPlan = percent(totalWithPlan, limit);

        boolean exceededNow = actualSpent.compareTo(limit) > 0;
        boolean exceededWithPlan = totalWithPlan.compareTo(limit) > 0;

        BudgetResponse r = new BudgetResponse();
        r.setBudgetId(b.getId());


        if (b.getCategory() == null) {
            r.setScope("TOTAL BUDGET");
        } else {
            r.setScope("CATEGORY BUDGET");
            r.setCategoryId(b.getCategory().getId());
            r.setCategoryName(b.getCategory().getName());
        }

        r.setMonth(b.getMonth());
        r.setYear(b.getYear());

        r.setBudgetLimit(limit);

        r.setActualExpenseAmount(actualSpent);
        r.setRemainingBudgetNow(remainingNow);
        r.setBudgetUsagePercentNow(percentNow);
        r.setExceededNow(exceededNow);


        r.setPlannedExpenseAmount(nullIfZero(plannedSpent));

        if (plannedSpent.signum() != 0) {
            r.setRemainingBudgetWithPlan(remainingWithPlan);
            r.setExceededWithPlan(exceededWithPlan);
            r.setBudgetUsagePercentWithPlan(percentWithPlan);
            r.setTotalExpenseWithPlan(totalWithPlan);
        }
        

        return r;
    }

    @Override
    public List<BudgetWarningResponse> checkExpenseAndWarnings(
            Users user,
            Long categoryId,
            String categoryName,
            BigDecimal convertedAmount,
            int month,
            int year) {

        List<BudgetWarningResponse> warnings = new ArrayList<>();
        Long userId = user.getId();

        // CATEGORY BUDGET
        budgetRepo.findByUser_IdAndCategory_IdAndMonthAndYear(
                userId, categoryId, month, year
        ).ifPresent(budget -> {

            BigDecimal actualBefore = transactionRepo.sumExpenseForBudget(
                    user, budget.getCategory(), month, year);
            if (actualBefore == null) actualBefore = BigDecimal.ZERO;

            BigDecimal spentAfter = actualBefore.add(convertedAmount);

            warnings.addAll(
                    buildWarning(
                            "CATEGORY",
                            categoryId,
                            categoryName,
                            budget.getLimitAmount(),
                            spentAfter
                    )
            );
        });

        // TOTAL BUDGET
        budgetRepo.findByUser_IdAndCategoryIsNullAndMonthAndYear(
                userId, month, year
        ).ifPresent(totalBudget -> {

            BigDecimal actualBefore = transactionRepo.sumExpenseForBudget(
                    user, null, month, year);
            if (actualBefore == null) actualBefore = BigDecimal.ZERO;

            BigDecimal spentAfter = actualBefore.add(convertedAmount);

            warnings.addAll(
                    buildWarning(
                            "TOTAL",
                            null,
                            null,
                            totalBudget.getLimitAmount(),
                            spentAfter
                    )
            );
        });

        return warnings;
    }

    private List<BudgetWarningResponse> buildWarning(
            String scope,
            Long categoryId,
            String categoryName,
            BigDecimal limit,
            BigDecimal spentAfter
    ) {

        List<BudgetWarningResponse> list = new ArrayList<>();

        if (limit == null || limit.signum() == 0) {
            return list;
        }

        BigDecimal remaining = limit.subtract(spentAfter);
        BigDecimal warnThreshold = limit.multiply(BigDecimal.valueOf(0.8));

        int percentUsed = spentAfter
                .multiply(BigDecimal.valueOf(100))
                .divide(limit, 0, RoundingMode.HALF_UP)
                .intValue();

        String label = scope.equals("TOTAL")
                ? "Total"
                : categoryName;

        BudgetWarningResponse w = new BudgetWarningResponse();
        w.setScope(scope);
        w.setCategoryId(categoryId);
        w.setCategoryName(categoryName);
        w.setLimit(limit);
        w.setSpent(spentAfter);
        w.setRemaining(remaining);
        w.setPercentUsed(percentUsed);

        if (spentAfter.compareTo(limit) > 0) {
            w.setWarningLevel(WarningLevel.EXCEEDED);
            w.setMessage(label + " budget exceeded");
        } else if (spentAfter.compareTo(warnThreshold) >= 0) {
            w.setWarningLevel(WarningLevel.WARNING);
            w.setMessage(label + " budget almost full");
        } else {
            w.setWarningLevel(WarningLevel.INFO);
            w.setMessage(label + " remaining budget: " + remaining);
        }

        list.add(w);
        return list;
    }


    private int percent(BigDecimal spent, BigDecimal limit) {

        if (limit == null || limit.signum() == 0) {
            return 0;
        }
        return spent
                .multiply(BigDecimal.valueOf(100))
                .divide(limit, 0, RoundingMode.HALF_UP)
                .intValue();
    }

    private BigDecimal nullIfZero(BigDecimal v) {
        return (v == null || v.signum() == 0) ? null : v;
    }

    private Integer nullIfZero(Integer v) {
        return (v == null || v == 0) ? null : v;
    }

    private Boolean nullIfFalse(Boolean v) {
        return (v == null || !v) ? null : v;
    }


}








