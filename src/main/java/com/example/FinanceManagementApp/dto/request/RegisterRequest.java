package com.example.FinanceManagementApp.dto.request;

import com.example.FinanceManagementApp.model.enums.CurrencyType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class RegisterRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;
    @NotBlank
    private String userName;

    @NotNull
    private CurrencyType baseCurrency;



}
