package com.example.FinanceManagementApp.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequest {

    @NotBlank(message = "refresh token can not blank")
    private String refreshToken;
}
