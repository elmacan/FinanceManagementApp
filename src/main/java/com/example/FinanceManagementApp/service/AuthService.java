package com.example.FinanceManagementApp.service;

import com.example.FinanceManagementApp.dto.request.LoginRequest;
import com.example.FinanceManagementApp.dto.request.RefreshTokenRequest;
import com.example.FinanceManagementApp.dto.request.RegisterRequest;
import com.example.FinanceManagementApp.dto.response.AuthResponse;
import jakarta.transaction.Transactional;

public interface AuthService {
    void register(RegisterRequest dto);

    AuthResponse verify(LoginRequest dto);

    AuthResponse refresh(RefreshTokenRequest dto);

    @Transactional
    void logout(Long userId);
}
