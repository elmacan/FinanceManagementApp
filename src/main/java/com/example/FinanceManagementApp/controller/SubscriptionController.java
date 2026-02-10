package com.example.FinanceManagementApp.controller;

import com.example.FinanceManagementApp.dto.request.SubscriptionRequest;
import com.example.FinanceManagementApp.dto.request.SubscriptionUpdateRequest;
import com.example.FinanceManagementApp.dto.response.SubscriptionResponse;
import com.example.FinanceManagementApp.security.CurrentUserPrincipal;
import com.example.FinanceManagementApp.service.SubscriptionServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {


    private final SubscriptionServiceImpl subscriptionService;


    @PostMapping
    public ResponseEntity<SubscriptionResponse >create(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @Valid @RequestBody SubscriptionRequest dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(subscriptionService.create(principal, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionResponse> get(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(subscriptionService.get(principal, id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SubscriptionResponse> update(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody SubscriptionUpdateRequest dto
    ) {
        return ResponseEntity.ok(subscriptionService.update(principal, id, dto));
    }

    @GetMapping
    public List<SubscriptionResponse> list(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @RequestParam(required = false) Boolean active
    ) {
        return subscriptionService.list(principal,active);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<SubscriptionResponse> activate(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(subscriptionService.activate(principal, id));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<SubscriptionResponse> deactivate(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(subscriptionService.deactivate(principal, id));
    }


}
