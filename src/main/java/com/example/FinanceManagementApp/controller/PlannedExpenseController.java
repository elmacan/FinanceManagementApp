package com.example.FinanceManagementApp.controller;


import com.example.FinanceManagementApp.dto.request.PlannedExpenseRequest;
import com.example.FinanceManagementApp.dto.response.PlannedExpenseResponse;
import com.example.FinanceManagementApp.dto.response.TransactionResponse;
import com.example.FinanceManagementApp.model.entity.PlannedExpense;
import com.example.FinanceManagementApp.security.CurrentUserPrincipal;
import com.example.FinanceManagementApp.service.impl.PlannedExpenseServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/planned-expenses")
@RequiredArgsConstructor
public class PlannedExpenseController {
    private final PlannedExpenseServiceImpl plannedExpenseService;



    @PostMapping
    public ResponseEntity<PlannedExpenseResponse> create(@AuthenticationPrincipal CurrentUserPrincipal principal,
                                                @Valid @RequestBody PlannedExpenseRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new PlannedExpenseResponse(plannedExpenseService.create(principal, dto)));
    }



    @GetMapping("/{id}")
    public PlannedExpenseResponse get(@AuthenticationPrincipal CurrentUserPrincipal principal,
                              @PathVariable Long id) {
        return new PlannedExpenseResponse(plannedExpenseService.get(principal, id));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<TransactionResponse> complete(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @PathVariable Long id) {
        TransactionResponse response = plannedExpenseService.complete(principal, id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PlannedExpenseResponse>> listAll(
            @AuthenticationPrincipal CurrentUserPrincipal principal
    ) {
        List<PlannedExpenseResponse> list = plannedExpenseService.listAll(principal);
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlannedExpenseResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody PlannedExpenseRequest dto,
            @AuthenticationPrincipal CurrentUserPrincipal principal) {

        PlannedExpense updated = plannedExpenseService.updatePlannedExpense(id, dto, principal);
        return ResponseEntity.ok(new PlannedExpenseResponse(updated));
    }

}
