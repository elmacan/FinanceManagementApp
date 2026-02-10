package com.example.FinanceManagementApp.service;

import com.example.FinanceManagementApp.dto.request.BudgetRequest;
import com.example.FinanceManagementApp.dto.response.BudgetResponse;
import com.example.FinanceManagementApp.dto.response.BudgetWarningResponse;
import com.example.FinanceManagementApp.model.entity.Users;
import com.example.FinanceManagementApp.security.CurrentUserPrincipal;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.util.List;

public interface BudgetService {
    @Transactional
    BudgetResponse create(@Valid BudgetRequest dto, CurrentUserPrincipal principal);

    BudgetResponse get(Long id, CurrentUserPrincipal principal);

    List<BudgetResponse> list(Integer month, Integer year, CurrentUserPrincipal principal);

    @Transactional
    BudgetResponse updateLimit(CurrentUserPrincipal principal, Long budgetId, BigDecimal newLimitAmount);

    List<BudgetWarningResponse> checkExpenseAndWarnings(
            Users user,
            Long categoryId,
            String categoryName,
            BigDecimal convertedAmount,
            int month,
            int year);
}

//dışarıdan çağrılan bir methodsa -> abstract olmalı
//helper iç methodsa hayır -> toResponse,buildwarning gibi

