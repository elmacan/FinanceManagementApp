package com.example.FinanceManagementApp.controller;


import com.example.FinanceManagementApp.dto.request.BillRequest;
import com.example.FinanceManagementApp.dto.response.BillPayResponse;
import com.example.FinanceManagementApp.dto.response.BillResponse;
import com.example.FinanceManagementApp.model.enums.BillStatus;
import com.example.FinanceManagementApp.security.CurrentUserPrincipal;
import com.example.FinanceManagementApp.service.impl.BillServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/bills")
public class BillController {


    private final BillServiceImpl billService;


    @PostMapping
    public ResponseEntity<BillResponse> create(@AuthenticationPrincipal CurrentUserPrincipal principal,
                                              @Valid @RequestBody BillRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(billService.create(principal, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillResponse> get(@AuthenticationPrincipal CurrentUserPrincipal principal,
                            @PathVariable Long id) {
        return ResponseEntity.ok(billService.get(principal, id));
    }

    @GetMapping
    public ResponseEntity<List<BillResponse>> list(@AuthenticationPrincipal CurrentUserPrincipal principal,
                                   @RequestParam(required = false) BillStatus status) {
        return ResponseEntity.ok(billService.list(principal, status));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<BillPayResponse> pay(
            @PathVariable Long id,
            @AuthenticationPrincipal CurrentUserPrincipal principal) {

        return ResponseEntity.ok(billService.pay(id, principal));
    }


}
