package com.example.FinanceManagementApp.dto.response;

import com.example.FinanceManagementApp.model.enums.WarningLevel;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)  //null olan alan jsona konmuyor
public class BudgetWarningResponse {

    private WarningLevel warningLevel;
    private String scope; // CATEGORY / TOTAL

    //ileride notification eklencek olursa
    private Long categoryId;
    private String categoryName;

    private BigDecimal limit;
    private BigDecimal spent;
    private BigDecimal remaining;

    private Integer percentUsed;

    private String message;



}
