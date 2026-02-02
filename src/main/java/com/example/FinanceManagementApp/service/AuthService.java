package com.example.FinanceManagementApp.service;

import com.example.FinanceManagementApp.dto.request.LoginRequest;
import com.example.FinanceManagementApp.dto.request.RefreshTokenRequest;
import com.example.FinanceManagementApp.dto.request.RegisterRequest;
import com.example.FinanceManagementApp.dto.response.AuthResponse;
import com.example.FinanceManagementApp.model.entity.RefreshToken;
import com.example.FinanceManagementApp.model.entity.Users;
import com.example.FinanceManagementApp.repository.RefreshTokenRepo;
import com.example.FinanceManagementApp.repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UsersRepo userRepo;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private RefreshTokenRepo refreshTokenRepo;


    public ResponseEntity<String> register(RegisterRequest dto) {

        if(userRepo.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Users user=new Users();
        user.setEmail(dto.getEmail());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setUserName(dto.getUserName());
        user.setBaseCurrency(dto.getBaseCurrency());

        userRepo.save(user);
        return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
    }

    public AuthResponse verify(LoginRequest dto) {


        try {

            authManager.authenticate(new UsernamePasswordAuthenticationToken(
                    dto.getEmail(),
                    dto.getPassword()));

            Users user=userRepo.findByEmail(dto.getEmail()) .orElseThrow(() -> new RuntimeException("User not found"));

            String accessToken = jwtService.generateAccessToken(user);

            String refreshToken =refreshTokenService.generateRefreshToken(user).getToken();


            return new AuthResponse(accessToken,refreshToken);


        } catch (Exception e) {

            throw new RuntimeException("Login failed");
        }


    }

    public AuthResponse refresh(RefreshTokenRequest dto) {
        RefreshToken refreshToken=refreshTokenRepo
                .findByToken(dto.getRefreshToken())
                .orElseThrow(() ->
                        new RuntimeException("Refresh token not found")
                );

        refreshTokenService.verifyExpiration(refreshToken);

        Users user=refreshToken.getUser();

        //rotation (kullanılan token)
        refreshTokenRepo.delete(refreshToken);

        String newAccessToken=jwtService.generateAccessToken(user);
        String newRefreshToken = refreshTokenService.generateRefreshToken(user).getToken();

        return new AuthResponse(newAccessToken, newRefreshToken);
    }

    public void logout(String email) {
        Users user=userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));


            System.out.println("User ID: " + user.getId());
            //userın tüm refresh tokenleri
            refreshTokenRepo.deleteByUser(user);


    }
}
