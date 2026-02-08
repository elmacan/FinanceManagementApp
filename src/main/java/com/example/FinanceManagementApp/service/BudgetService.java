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
import com.example.FinanceManagementApp.repository.TransactionRepo;
import com.example.FinanceManagementApp.security.CurrentUserPrincipal;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepo budgetRepo;
    @Autowired
    private  CurrentUserService currentUserService;
    @Autowired
    private CategoryRepo categoryRepo;
    @Autowired
    private TransactionRepo transactionRepo;




    @Transactional
    public BudgetResponse create(@Valid BudgetRequest dto, CurrentUserPrincipal principal) {
        Users user=currentUserService.getCurrentUser(principal);

        //nullsa total budget
        Category category=null;

        if (dto.getCategoryId() != null) {

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
                        "Budget already exists");
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

    public BudgetResponse get(Long id, CurrentUserPrincipal principal) {
        Long userId = currentUserService.getCurrentUserId(principal);

        Budget b = budgetRepo.findByIdAndUser_Id(id, userId)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND, "Budget not found"));

        return toResponse(b, b.getUser());
    }



    public List<BudgetResponse> list(Integer month, Integer year, CurrentUserPrincipal principal) {
        Users user=currentUserService.getCurrentUser(principal);

        List<Budget> list = (month != null && year != null)
                ? budgetRepo.findByUserAndMonthAndYear(user, month, year)
                : budgetRepo.findByUser(user);

        return list.stream()
                .map(b -> toResponse(b, user))
                .toList();
    }


    @Transactional
    public BudgetResponse updateLimit(CurrentUserPrincipal principal, Long budgetId, BigDecimal newLimitAmount) {

        if (newLimitAmount == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "limitAmount is required");
        }

        Long userId = currentUserService.getCurrentUser(principal).getId();

        Budget budget = budgetRepo.findByIdAndUser_Id(budgetId, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Budget not found"));

        budget.setLimitAmount(newLimitAmount);

        return toResponse(budgetRepo.save(budget), budget.getUser());

    }

    private BudgetResponse toResponse(Budget b, Users user) {

        BigDecimal spent = transactionRepo.sumExpenseForBudget(
                user,
                b.getCategory(),
                b.getMonth(),
                b.getYear()
        );

        BigDecimal remaining = b.getLimitAmount().subtract(spent);

        int percent = spent
                .multiply(BigDecimal.valueOf(100))
                .divide(b.getLimitAmount(), 0, RoundingMode.HALF_UP)
                .intValue();

        BudgetResponse r = new BudgetResponse();
        r.setBudgetId(b.getId());

        if (b.getCategory() == null) {
            r.setScope("TOTAL BUDGET");
        } else {
            r.setScope("CATEGORY BUDGET");
            r.setCategoryId(b.getCategory().getId());
            r.setCategoryName(b.getCategory().getName());
        }

        r.setLimit(b.getLimitAmount());
        r.setMonth(b.getMonth());
        r.setYear(b.getYear());
        r.setSpentAmount(spent);
        r.setRemainingAmount(remaining);
        r.setPercentUsed(percent);
        r.setExceeded(remaining.signum() < 0);

        return r;

    }

    public List<BudgetWarningResponse> checkExpenseAndWarnings(Users user, Long categoryId, String categoryName, BigDecimal convertedAmount, int month, int year) {

        List<BudgetWarningResponse> warnings = new ArrayList<>();
        Long userId = user.getId();

        budgetRepo.findByUser_IdAndCategory_IdAndMonthAndYear(
                userId, categoryId, month, year
        ).ifPresent(budget -> {

            BigDecimal spentBefore = transactionRepo
                    .sumExpenseForBudget(user,
                            budget.getCategory(),
                            month,
                            year);

            BigDecimal spentAfter =
                    spentBefore.add(convertedAmount);

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

        budgetRepo.findByUser_IdAndCategoryIsNullAndMonthAndYear(
                userId, month, year
        ).ifPresent(totalBudget -> {

            BigDecimal spentBefore =
                    transactionRepo.sumExpenseForBudget(
                            user,
                            null,
                            month,
                            year
                    );

            BigDecimal spentAfter =
                    spentBefore.add(convertedAmount);

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

        BigDecimal remaining = limit.subtract(spentAfter);

        int percentUsed = spentAfter
                .multiply(BigDecimal.valueOf(100))
                .divide(limit, 0, RoundingMode.HALF_UP)
                .intValue();

        if (spentAfter.compareTo(limit) > 0) {

            BudgetWarningResponse w = new BudgetWarningResponse();
            w.setWarningLevel(WarningLevel.EXCEEDED);
            w.setScope(scope);
            w.setLimit(limit);
            w.setSpent(spentAfter);
            w.setRemaining(remaining);
            w.setPercentUsed(percentUsed);
            w.setMessage(scope + " budget exceeded");

            list.add(w);
            return list;
        }

        BigDecimal warnThreshold = limit.multiply(BigDecimal.valueOf(0.8));

        if (spentAfter.compareTo(warnThreshold) >= 0) {

            BudgetWarningResponse w = new BudgetWarningResponse();
            w.setWarningLevel(WarningLevel.WARNING);
            w.setScope(scope);
            w.setCategoryId(categoryId);
            w.setCategoryName(categoryName);
            w.setLimit(limit);
            w.setSpent(spentAfter);
            w.setRemaining(remaining);
            w.setPercentUsed(percentUsed);
            w.setMessage(scope + " budget almost full");

            list.add(w);
        }

        return list;
    }



}

