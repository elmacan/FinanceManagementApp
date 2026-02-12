package com.example.FinanceManagementApp.dto.response.report;

import com.example.FinanceManagementApp.model.enums.BillStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillReportResponse {

    private int totalBills;
    private int paidCount;
    private int unpaidCount;
    private int overdueCount;

    private BigDecimal totalUnpaidAmount;

    private List<BillItem> unpaidBills;
    private List<BillItem> overDueBills;
    private List<BillItem> paidBills;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BillItem {
        private Long id;
        private String name;
        private BigDecimal amount;
        private LocalDate dueDate;
        private BillStatus status;
    }
}