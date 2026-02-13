package com.example.FinanceManagementApp.service;

import com.example.FinanceManagementApp.dto.response.BudgetResponse;
import com.example.FinanceManagementApp.dto.response.report.*;
import com.example.FinanceManagementApp.security.CurrentUserPrincipal;

import java.util.List;

public interface ReportService {
    MonthlySummaryResponse monthlySummary(CurrentUserPrincipal p, int month, int year);

    ExpenseCategoryResponse buildExpenseCategoryReport(CurrentUserPrincipal p, int month, int year);

    ThreeMonthTrendResponse threeMonthTrend(CurrentUserPrincipal principal);

    List<BudgetResponse> buildBudgetReport(Integer month, Integer year, CurrentUserPrincipal p);

    BillReportResponse buildBillReport(CurrentUserPrincipal p);

    SubscriptionReportResponse buildSubscriptionReport(CurrentUserPrincipal p);

    SavingGoalWithEntityResponse buildSavingGoalReport(CurrentUserPrincipal principal, Long goalId);

    List<SavingGoalSummaryResponse> allSavingGoalsReport(CurrentUserPrincipal principal);

    PlannedExpenseReportResponse buildPlannedReport(CurrentUserPrincipal principal, int month, int year);
}
