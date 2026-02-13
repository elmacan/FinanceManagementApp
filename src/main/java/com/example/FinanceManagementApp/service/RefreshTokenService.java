package com.example.FinanceManagementApp.service;

import com.example.FinanceManagementApp.model.entity.RefreshToken;
import com.example.FinanceManagementApp.model.entity.Users;

public interface RefreshTokenService {
    RefreshToken generateRefreshToken(Users user);

    void verifyExpiration(RefreshToken token);
}
