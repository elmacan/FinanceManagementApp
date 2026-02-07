package com.example.FinanceManagementApp.dto.request;


import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ExpenseRequest  extends BaseTransactionRequest{

    @NotNull
    private Long categoryId;

}
