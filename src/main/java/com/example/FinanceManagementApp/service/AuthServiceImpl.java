package com.example.FinanceManagementApp.service;

import com.example.FinanceManagementApp.dto.request.LoginRequest;
import com.example.FinanceManagementApp.dto.request.RefreshTokenRequest;
import com.example.FinanceManagementApp.dto.request.RegisterRequest;
import com.example.FinanceManagementApp.dto.response.AuthResponse;
import com.example.FinanceManagementApp.exception.ApiException;
import com.example.FinanceManagementApp.model.entity.RefreshToken;
import com.example.FinanceManagementApp.model.entity.Users;
import com.example.FinanceManagementApp.repository.RefreshTokenRepo;
import com.example.FinanceManagementApp.repository.UsersRepo;
import com.example.FinanceManagementApp.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

        private final UsersRepo userRepo;

        private final AuthenticationManager authManager;

        private final PasswordEncoder encoder;

        private final JwtService jwtService;

        private final RefreshTokenService refreshTokenService;
        private final RefreshTokenRepo refreshTokenRepo;

        private final SubscriptionServiceImpl subscriptionService;


    @Override
    public void register(RegisterRequest dto) {

        if(userRepo.existsByEmail(dto.getEmail())) {
            throw new ApiException(HttpStatus.CONFLICT, "Email already exists");
        }

        Users user=new Users();
        user.setEmail(dto.getEmail());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setUserName(dto.getUserName());
        user.setBaseCurrency(dto.getBaseCurrency());

        userRepo.save(user);

    }

    @Override
    public AuthResponse verify(LoginRequest dto) {

            authManager.authenticate(new UsernamePasswordAuthenticationToken(
                    dto.getEmail(),
                    dto.getPassword()));

            Users user=userRepo.findByEmail(dto.getEmail())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

            String accessToken = jwtService.generateAccessToken(user);

            String refreshToken =refreshTokenService.generateRefreshToken(user).getToken();

            //login sonrası catch-up
            subscriptionService.generateMissingSubscriptionTransactions(user);


            return new AuthResponse(accessToken,refreshToken);

    }

    @Override
    public AuthResponse refresh(RefreshTokenRequest dto) {
        RefreshToken refreshToken=refreshTokenRepo
                .findByToken(dto.getRefreshToken())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Refresh token not found"));

        refreshTokenService.verifyExpiration(refreshToken);

        Users user=refreshToken.getUser();

        //rotation (kullanılan token)
        refreshTokenRepo.delete(refreshToken);

        String newAccessToken=jwtService.generateAccessToken(user);
        String newRefreshToken = refreshTokenService.generateRefreshToken(user).getToken();

        return new AuthResponse(newAccessToken, newRefreshToken);
    }

    @Transactional
    @Override
    public void logout(Long userId) {

        System.out.println("User ID: " + userId+ " logged out");
            //userın tüm refresh tokenleri
            refreshTokenRepo.deleteByUser_Id(userId);


    }
}
