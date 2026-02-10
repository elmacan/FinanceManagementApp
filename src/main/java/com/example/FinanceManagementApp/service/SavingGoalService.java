package com.example.FinanceManagementApp.service;

import com.example.FinanceManagementApp.dto.request.SavingEntryRequest;
import com.example.FinanceManagementApp.dto.request.SavingGoalRequest;
import com.example.FinanceManagementApp.dto.request.SavingGoalUpdateRequest;
import com.example.FinanceManagementApp.dto.response.SavingEntryResponse;
import com.example.FinanceManagementApp.dto.response.SavingGoalResponse;
import com.example.FinanceManagementApp.security.CurrentUserPrincipal;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import java.util.List;

public interface SavingGoalService {
    @Transactional
    SavingGoalResponse create(CurrentUserPrincipal p, @Valid SavingGoalRequest dto);

    SavingGoalResponse get(CurrentUserPrincipal p, Long id);

    @Transactional
    SavingGoalResponse update(CurrentUserPrincipal principal, Long id, @Valid SavingGoalUpdateRequest dto);

    List<SavingGoalResponse> list(CurrentUserPrincipal p);

    @Transactional
    SavingEntryResponse addEntry(CurrentUserPrincipal p,
                                 Long goalId,
                                 SavingEntryRequest dto);

    List<SavingEntryResponse> listEntries(CurrentUserPrincipal p, Long goalId);
}
