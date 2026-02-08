package com.example.FinanceManagementApp.controller;


import com.example.FinanceManagementApp.dto.request.BudgetLimitRequest;
import com.example.FinanceManagementApp.dto.request.BudgetRequest;
import com.example.FinanceManagementApp.dto.response.BudgetResponse;
import com.example.FinanceManagementApp.model.entity.Budget;
import com.example.FinanceManagementApp.security.CurrentUserPrincipal;
import com.example.FinanceManagementApp.service.BudgetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/budgets")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;



    @PostMapping
    public ResponseEntity<BudgetResponse> create(
            @Valid @RequestBody BudgetRequest dto,
            @AuthenticationPrincipal CurrentUserPrincipal principal) {

        return ResponseEntity.status(HttpStatus.CREATED).body(budgetService.create(dto, principal));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BudgetResponse> get(
            @PathVariable Long id,
            @AuthenticationPrincipal CurrentUserPrincipal principal) {
        return ResponseEntity.ok(budgetService.get(id, principal));
    }

    @PatchMapping("/{id}/limit")
    public ResponseEntity<BudgetResponse> updateLimit(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @PathVariable Long id,
            @RequestBody @Valid BudgetLimitRequest dto
    ) {
        return ResponseEntity.ok(budgetService.updateLimit(principal, id, dto.getNewLimitAmount()));
    }



    @GetMapping
    public List<BudgetResponse> list(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @AuthenticationPrincipal CurrentUserPrincipal principal) {
        return budgetService.list(month, year, principal);
    }




}
