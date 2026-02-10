package com.example.FinanceManagementApp.service;

import com.example.FinanceManagementApp.dto.request.PlannedExpenseRequest;
import com.example.FinanceManagementApp.dto.response.PlannedExpenseResponse;
import com.example.FinanceManagementApp.dto.response.TransactionResponse;
import com.example.FinanceManagementApp.model.entity.PlannedExpense;
import com.example.FinanceManagementApp.security.CurrentUserPrincipal;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import java.util.List;

public interface PlannedExpenseService {
    @Transactional
    PlannedExpense create(CurrentUserPrincipal principal, @Valid PlannedExpenseRequest dto);

    PlannedExpense get(CurrentUserPrincipal principal, Long id);

    List<PlannedExpenseResponse> listAll(CurrentUserPrincipal principal);

    @Transactional
    TransactionResponse complete(CurrentUserPrincipal principal, Long id);

    PlannedExpense updatePlannedExpense(Long id, @Valid PlannedExpenseRequest dto, CurrentUserPrincipal principal);
}
