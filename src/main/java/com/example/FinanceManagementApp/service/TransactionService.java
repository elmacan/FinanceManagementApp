package com.example.FinanceManagementApp.service;

import com.example.FinanceManagementApp.dto.request.ExpenseRequest;
import com.example.FinanceManagementApp.dto.request.IncomeRequest;
import com.example.FinanceManagementApp.dto.response.TransactionResponse;
import com.example.FinanceManagementApp.model.entity.*;
import com.example.FinanceManagementApp.model.enums.TransactionSourceType;
import com.example.FinanceManagementApp.model.enums.TransactionType;
import com.example.FinanceManagementApp.security.CurrentUserPrincipal;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;

public interface TransactionService {
    @Transactional
    TransactionResponse createExpense(CurrentUserPrincipal principal, @Valid ExpenseRequest dto);

    @Transactional
    TransactionResponse createIncome(CurrentUserPrincipal principal, @Valid IncomeRequest dto);

    TransactionResponse get(CurrentUserPrincipal principal, Long id);

    List<TransactionResponse> list(
            CurrentUserPrincipal principal,
            Integer month,
            Integer year,
            TransactionType type,
            Long categoryId,
            TransactionSourceType sourceType,
            LocalDate from,
            LocalDate to
    );

    @Transactional
    TransactionResponse createFromBill(Bill bill, Users user);

    @Transactional
    void createFromSubscription(
            Subscription sub,
            Users user,
            LocalDate date
    );

    //KONTROL
    @Transactional
    TransactionResponse createFromPlannedExpense(PlannedExpense pe, Users user);
}
