package com.example.FinanceManagementApp.service;

import com.example.FinanceManagementApp.dto.request.SubscriptionRequest;
import com.example.FinanceManagementApp.dto.request.SubscriptionUpdateRequest;
import com.example.FinanceManagementApp.dto.response.SubscriptionResponse;
import com.example.FinanceManagementApp.model.entity.Users;
import com.example.FinanceManagementApp.security.CurrentUserPrincipal;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import java.util.List;

public interface SubscriptionService {
    @Transactional
    SubscriptionResponse create(CurrentUserPrincipal principal, @Valid SubscriptionRequest dto);

    SubscriptionResponse get(CurrentUserPrincipal principal, Long id);

    List<SubscriptionResponse> list(CurrentUserPrincipal principal, Boolean active);

    @Transactional
    SubscriptionResponse update(CurrentUserPrincipal principal, Long id, @Valid SubscriptionUpdateRequest dto);

    @Transactional
    SubscriptionResponse activate(CurrentUserPrincipal principal, Long id);

    @Transactional
    SubscriptionResponse deactivate(CurrentUserPrincipal principal, Long id);

    //catch-up
    @Transactional
    void generateMissingSubscriptionTransactions(Users user);
}
