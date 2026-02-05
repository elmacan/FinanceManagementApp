package com.example.FinanceManagementApp.dto.request;

import com.example.FinanceManagementApp.model.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoryRequest {

    @NotBlank
    private String name;

    @NotNull
    private TransactionType type;

}
