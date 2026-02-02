package com.example.FinanceManagementApp.controller;

import com.example.FinanceManagementApp.dto.request.LoginRequest;
import com.example.FinanceManagementApp.dto.request.RegisterRequest;
import com.example.FinanceManagementApp.dto.response.AuthResponse;
import com.example.FinanceManagementApp.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequest dto){

        return authService.register(dto);
    }


    @PostMapping("login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest dto){

        return authService.verify(dto);
    }



}
