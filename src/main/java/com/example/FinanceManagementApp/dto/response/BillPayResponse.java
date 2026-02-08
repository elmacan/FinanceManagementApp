package com.example.FinanceManagementApp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BillPayResponse {

    private BillResponse bill;
    private TransactionResponse transaction;
}
