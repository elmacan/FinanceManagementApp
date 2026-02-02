package com.example.FinanceManagementApp.service;

import com.example.FinanceManagementApp.dto.request.LoginRequest;
import com.example.FinanceManagementApp.dto.request.RegisterRequest;
import com.example.FinanceManagementApp.dto.response.AuthResponse;
import com.example.FinanceManagementApp.model.entity.Users;
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

    public ResponseEntity<AuthResponse> verify(LoginRequest dto) {


        try {

            authManager.authenticate(new UsernamePasswordAuthenticationToken(
                    dto.getEmail(),
                    dto.getPassword()));

            Users user=userRepo.findByEmail(dto.getEmail()) .orElseThrow(() -> new RuntimeException("User not found"));;

            String accessToken = jwtService.generateToken(user);

            String refreshToken =refreshTokenService.generateRefreshToken(user);


            return new ResponseEntity<>(new AuthResponse(accessToken,refreshToken),HttpStatus.OK);


        } catch (Exception e) {

            throw new RuntimeException("Login failed");
        }


    }
}
