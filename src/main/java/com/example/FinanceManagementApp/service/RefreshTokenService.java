package com.example.FinanceManagementApp.service;


import com.example.FinanceManagementApp.model.entity.RefreshToken;
import com.example.FinanceManagementApp.model.entity.Users;
import com.example.FinanceManagementApp.repository.RefreshTokenRepo;
import com.example.FinanceManagementApp.repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private static final long REFRESH_TOKEN_DURATION =
            1000L * 60 * 60 * 24 ; // 1gün

    @Autowired
    private RefreshTokenRepo refreshTokenRepo;


    public RefreshToken generateRefreshToken(Users user) {
        RefreshToken refreshToken=new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(
                Instant.now().plusMillis(REFRESH_TOKEN_DURATION)
        );

        return refreshTokenRepo.save(refreshToken);

    }

    public void verifyExpiration(RefreshToken token) {

        if (token.getExpiryDate().isBefore(Instant.now())) {

            refreshTokenRepo.delete(token);  //o anki refresh token siliniyor süresi geçince
            throw new RuntimeException("Refresh token expired");
        }

    }
}
