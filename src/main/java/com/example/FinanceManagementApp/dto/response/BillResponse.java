package com.example.FinanceManagementApp.dto.response;

import com.example.FinanceManagementApp.model.entity.Bill;
import com.example.FinanceManagementApp.model.enums.BillStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;



@Data
public class BillResponse {
    private Long id;
    private String name;
    private Long categoryId;
    private String categoryName;

    private BigDecimal amount;
    private LocalDate dueDate;
    private BillStatus status;


    public BillResponse(Bill bill) {
        this.id = bill.getId();
        this.name = bill.getName();
        this.categoryId = bill.getCategory().getId();
        this.categoryName = bill.getCategory().getName();
        this.amount = bill.getAmount();
        this.dueDate = bill.getDueDate();

        if (bill.getStatus() == BillStatus.UNPAID &&
                bill.getDueDate() != null &&
                bill.getDueDate().isBefore(LocalDate.now())) {

            this.status = BillStatus.OVERDUE;
        } else {
            this.status = bill.getStatus();
        }


    }

}
