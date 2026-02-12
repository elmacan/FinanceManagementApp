package com.example.FinanceManagementApp.controller;

import com.example.FinanceManagementApp.dto.response.BudgetResponse;
import com.example.FinanceManagementApp.dto.response.report.*;
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

        int[] my=resolveMonthYear(month, year);
        return ResponseEntity.ok(reportService.monthlySummary(p, my[0], my[1]));
    }

    @Operation(
            summary = "Kategori bazlı harcama dağılımı",
            description = "Ay veya yıl girilmezse, otomatik şu ankileri alır"
    )
    @GetMapping("/category")
    public ResponseEntity<ExpenseCategoryResponse>category(
            @AuthenticationPrincipal CurrentUserPrincipal p,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year
    ) {

        int[] my=resolveMonthYear(month, year);
        return ResponseEntity.ok(reportService.buildExpenseCategoryReport(p, my[0], my[1]));
    }

    @GetMapping("/three-month-trend")
    public ResponseEntity<ThreeMonthTrendResponse> trend(@AuthenticationPrincipal CurrentUserPrincipal p) {
        return ResponseEntity.ok(reportService.threeMonthTrend(p));
    }

    @Operation(
            description = "Ay veya yıl girilmezse, otomatik şu ankileri alır"
    )
    @GetMapping("/budget")
    public ResponseEntity<List<BudgetResponse>> budgetReport(
            @AuthenticationPrincipal CurrentUserPrincipal p,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year
    ) {
        int[] my=resolveMonthYear(month, year);
        return ResponseEntity.ok(reportService.buildBudgetReport(my[0], my[1], p));
    }


    @GetMapping("/bills")
    public ResponseEntity<BillReportResponse> billReport(
            @AuthenticationPrincipal CurrentUserPrincipal p
    ) {
        return ResponseEntity.ok(reportService.buildBillReport(p));
    }

    @Operation(description = "Tüm Aboneliker ve Abonelik Aylık Maliyet ve Tahsilat Durumu")
    @GetMapping("/subscriptions")
    public ResponseEntity<SubscriptionReportResponse> subscriptionReport(
            @AuthenticationPrincipal CurrentUserPrincipal p
    ) {
        return ResponseEntity.ok(reportService.buildSubscriptionReport(p));
    }


    //default olarak şu ankileri çekiyor
    private int[] resolveMonthYear(Integer month, Integer year) {
        LocalDate now = LocalDate.now();

        if (month == null) month = now.getMonthValue();
        if (year == null) year = now.getYear();

        return new int[]{month, year};
    }




}




