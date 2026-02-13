package com.example.FinanceManagementApp.service.impl;


import com.example.FinanceManagementApp.exception.ApiException;
import com.example.FinanceManagementApp.model.entity.RefreshToken;
import com.example.FinanceManagementApp.model.entity.Users;
import com.example.FinanceManagementApp.repository.RefreshTokenRepo;
import com.example.FinanceManagementApp.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;
@RequiredArgsConstructor
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private static final long REFRESH_TOKEN_DURATION =
            1000L * 60 * 60 * 24 ; // 1gün


    private final RefreshTokenRepo refreshTokenRepo;


    @Override
    public RefreshToken generateRefreshToken(Users user) {
        RefreshToken refreshToken=new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(
                Instant.now().plusMillis(REFRESH_TOKEN_DURATION)
        );

        return refreshTokenRepo.save(refreshToken);

    }

    @Override
    public void verifyExpiration(RefreshToken token) {

        if (token.getExpiryDate().isBefore(Instant.now())) {

            refreshTokenRepo.delete(token);  //o anki refresh token siliniyor süresi gecince
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Refresh token expired");
        }

    }
}
