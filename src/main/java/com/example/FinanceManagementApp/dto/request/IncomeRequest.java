package com.example.FinanceManagementApp.dto.request;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class IncomeRequest extends BaseTransactionRequest{

    private Long categoryId; // opsiyonel

}
