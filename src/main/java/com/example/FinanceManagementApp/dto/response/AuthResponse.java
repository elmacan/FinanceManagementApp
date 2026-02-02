package com.example.FinanceManagementApp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
public class AuthResponse {
    private String accessToken;
    private String refreshToken;


}
