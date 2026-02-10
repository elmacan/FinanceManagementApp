package com.example.FinanceManagementApp.service;

import com.example.FinanceManagementApp.dto.request.BillRequest;
import com.example.FinanceManagementApp.dto.response.BillPayResponse;
import com.example.FinanceManagementApp.dto.response.BillResponse;
import com.example.FinanceManagementApp.model.enums.BillStatus;
import com.example.FinanceManagementApp.security.CurrentUserPrincipal;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import java.util.List;

public interface BillService {
    @Transactional
    BillResponse create(CurrentUserPrincipal principal, @Valid BillRequest dto);

    BillResponse get(CurrentUserPrincipal principal, Long id);

    List<BillResponse> list(CurrentUserPrincipal principal, BillStatus status);

    @Transactional
    BillPayResponse pay(Long billId, CurrentUserPrincipal principal);
}
