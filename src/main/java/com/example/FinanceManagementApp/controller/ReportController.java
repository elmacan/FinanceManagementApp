package com.example.FinanceManagementApp.controller;

import com.example.FinanceManagementApp.dto.response.BudgetResponse;
import com.example.FinanceManagementApp.dto.response.report.*;
import com.example.FinanceManagementApp.security.CurrentUserPrincipal;
import com.example.FinanceManagementApp.service.impl.ReportServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportServiceImpl reportService;


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
            summary = "Kategori Bazlı Harcama Dağılımı",
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
            summary = "Kategori ve Toplam Bütçe için Limit ve Harcama Durumu",
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

    @Operation(summary = "Tüm Faturalar ve Ödeme Durumları")
    @GetMapping("/bills")
    public ResponseEntity<BillReportResponse> billReport(
            @AuthenticationPrincipal CurrentUserPrincipal p
    ) {
        return ResponseEntity.ok(reportService.buildBillReport(p));
    }

    @Operation(summary = "Tüm Aboneliker ve Abonelik Aylık Maliyet ve Tahsilat Durumu")
    @GetMapping("/subscriptions")
    public ResponseEntity<SubscriptionReportResponse> subscriptionReport(
            @AuthenticationPrincipal CurrentUserPrincipal p
    ) {
        return ResponseEntity.ok(reportService.buildSubscriptionReport(p));
    }

    @Operation(
            summary = "Spesifik Birikim Hedefi Raporu"
    )
    @GetMapping("/saving-goal")
    public ResponseEntity<SavingGoalWithEntityResponse> savingGoalReport(
            @AuthenticationPrincipal CurrentUserPrincipal p,
            @RequestParam Long goalId
    ) {
        return ResponseEntity.ok(reportService.buildSavingGoalReport(p,goalId));
    }

    @Operation(
            summary = "Tüm Birikim Hedefleri ve Durumları"
    )
    @GetMapping("/all-saving-goals")
    public ResponseEntity<List<SavingGoalSummaryResponse>> allSavingGoalsReport(
            @AuthenticationPrincipal CurrentUserPrincipal p
    ) {
        return ResponseEntity.ok(reportService.allSavingGoalsReport(p));
    }

    @Operation(summary = "Aylık Planlı Harcamalar için Özet ve Analiz",
            description = "Ay veya yıl girilmezse, otomatik şu ankileri alır")
    @GetMapping("/planned-expenses")
    public ResponseEntity<PlannedExpenseReportResponse> plannedReport(
            @AuthenticationPrincipal CurrentUserPrincipal p,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year
    ) {

        int[] my=resolveMonthYear(month, year);
        return ResponseEntity.ok(reportService.buildPlannedReport(p, my[0], my[1]));
    }



    //default olarak şu ankileri çekiyor
    private int[] resolveMonthYear(Integer month, Integer year) {
        LocalDate now = LocalDate.now();

        if (month == null) month = now.getMonthValue();
        if (year == null) year = now.getYear();

        return new int[]{month, year};
    }





}




