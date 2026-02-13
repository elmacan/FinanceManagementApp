package com.example.FinanceManagementApp.controller;

import com.example.FinanceManagementApp.dto.request.ExpenseRequest;
import com.example.FinanceManagementApp.dto.request.IncomeRequest;
import com.example.FinanceManagementApp.dto.response.TransactionResponse;
import com.example.FinanceManagementApp.model.enums.TransactionSourceType;
import com.example.FinanceManagementApp.model.enums.TransactionType;
import com.example.FinanceManagementApp.security.CurrentUserPrincipal;
import com.example.FinanceManagementApp.service.impl.TransactionServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
@RequiredArgsConstructor
@RestController
@RequestMapping("api/transactions")
public class TransactionController {


    private final TransactionServiceImpl transactionService;


    @PostMapping("/expense")
    public ResponseEntity<TransactionResponse> createExpense(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @RequestBody @Valid ExpenseRequest dto
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transactionService.createExpense(principal, dto));
    }

    @PostMapping("/income")
    public ResponseEntity<TransactionResponse> createIncome(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @RequestBody @Valid IncomeRequest dto
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transactionService.createIncome(principal, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> get(
            @PathVariable Long id,
            @AuthenticationPrincipal CurrentUserPrincipal principal
    ) {

        return ResponseEntity.ok( transactionService.get(principal, id));
    }

    @GetMapping
    public List<TransactionResponse> list(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) TransactionSourceType sourceType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return transactionService.list(principal, month, year, type, categoryId, sourceType, from, to);
    }

}
