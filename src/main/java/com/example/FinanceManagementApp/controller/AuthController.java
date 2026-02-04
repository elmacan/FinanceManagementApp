package com.example.FinanceManagementApp.controller;

import com.example.FinanceManagementApp.dto.request.LoginRequest;
import com.example.FinanceManagementApp.dto.request.RefreshTokenRequest;
import com.example.FinanceManagementApp.dto.request.RegisterRequest;
import com.example.FinanceManagementApp.dto.response.AuthResponse;
import com.example.FinanceManagementApp.security.CurrentUserPrincipal;
import com.example.FinanceManagementApp.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
public class AuthController {
    //http statusler controller içinde olmalı

    @Autowired
    AuthService authService;

    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequest dto){

        authService.register(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED) //  201
                .build();
    }


    @PostMapping("login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest dto){

        AuthResponse response=authService.verify(dto);
        return ResponseEntity.ok(response);  //200
    }


    @PostMapping("refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody @Valid RefreshTokenRequest dto){

        AuthResponse response = authService.refresh(dto);

        return ResponseEntity.ok(response); //200
    }


    @PostMapping("logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal CurrentUserPrincipal user) {


        authService.logout(user.getId());

        return ResponseEntity.noContent().build(); //204 genelde deletelerde
    }



    @GetMapping("test")
    public ResponseEntity<String> test(){
        String response="test is correct";
        return ResponseEntity.ok(response);
    }


}
