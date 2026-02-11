package com.example.FinanceManagementApp.controller;

import com.example.FinanceManagementApp.dto.response.report.CategoryDistributionResponse;
import com.example.FinanceManagementApp.dto.response.report.MonthlySummaryResponse;
import com.example.FinanceManagementApp.security.CurrentUserPrincipal;
import com.example.FinanceManagementApp.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/monthly-summary")
    @Operation(
            summary = "Aylık Gelir/Gider Raporu",
            description = "Belirtilen ay için toplam gelir, gider finansal özet raporu"
    )
    public ResponseEntity<MonthlySummaryResponse> monthly(
            @AuthenticationPrincipal CurrentUserPrincipal p,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year
    ) {
        //default oalrak bu ay
        if (month == null || year == null) {
            LocalDate now = LocalDate.now();
            month = now.getMonthValue();
            year = now.getYear();
        }

        return ResponseEntity.ok(reportService.monthlySummary(p, month, year));
    }

    @GetMapping("/category")
    public ResponseEntity<List<CategoryDistributionResponse>>category(
            @AuthenticationPrincipal CurrentUserPrincipal p,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year
    ) {

        if (month == null || year == null) {
            LocalDate now = LocalDate.now();
            month = now.getMonthValue();
            year = now.getYear();
        }
        return ResponseEntity.ok(reportService.categoryDistribution(p, month, year));
    }
}




