package com.example.FinanceManagementApp.controller;

import com.example.FinanceManagementApp.dto.request.SavingEntryRequest;
import com.example.FinanceManagementApp.dto.request.SavingGoalRequest;
import com.example.FinanceManagementApp.dto.request.SavingGoalUpdateRequest;
import com.example.FinanceManagementApp.dto.response.SavingEntryResponse;
import com.example.FinanceManagementApp.dto.response.SavingGoalResponse;
import com.example.FinanceManagementApp.security.CurrentUserPrincipal;
import com.example.FinanceManagementApp.service.impl.SavingGoalServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/saving-goals")
@RequiredArgsConstructor
public class SavingGoalController {

    private final SavingGoalServiceImpl savingGoalService;


    @PostMapping
    public ResponseEntity<SavingGoalResponse> create(
            @AuthenticationPrincipal CurrentUserPrincipal p,
            @RequestBody @Valid SavingGoalRequest dto) {


        return ResponseEntity.status(HttpStatus.CREATED).body(savingGoalService.create(p, dto));
    }


    @GetMapping("/{id}")
    public ResponseEntity<SavingGoalResponse> get(
            @AuthenticationPrincipal CurrentUserPrincipal p,
            @PathVariable Long id) {
        return ResponseEntity.ok(savingGoalService.get(p,id));
    }


    @PutMapping("/{id}")
    public ResponseEntity<SavingGoalResponse> update(@AuthenticationPrincipal CurrentUserPrincipal principal,
                             @PathVariable Long id,
                             @Valid @RequestBody SavingGoalUpdateRequest dto) {
        return ResponseEntity.ok(savingGoalService.update(principal, id, dto));
    }

    @GetMapping
    public ResponseEntity<List<SavingGoalResponse>> list(
            @AuthenticationPrincipal CurrentUserPrincipal p) {
        return ResponseEntity.ok(savingGoalService.list(p));
    }


    @PostMapping("/{id}/entries")
    public ResponseEntity<SavingEntryResponse> addEntry(
            @AuthenticationPrincipal CurrentUserPrincipal p,
            @PathVariable Long id,
            @Valid @RequestBody SavingEntryRequest dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savingGoalService.addEntry(p,id,dto));
    }


    @GetMapping("/{id}/entries")
    public ResponseEntity<List<SavingEntryResponse>> listEntries(
            @AuthenticationPrincipal CurrentUserPrincipal p,
            @PathVariable Long id) {

        return ResponseEntity.ok(savingGoalService.listEntries(p,id));
    }



    }




