package com.example.FinanceManagementApp.controller;

import com.example.FinanceManagementApp.dto.request.LoginRequest;
import com.example.FinanceManagementApp.dto.request.UpdateUserRequest;
import com.example.FinanceManagementApp.dto.response.UserResponse;
import com.example.FinanceManagementApp.model.entity.Users;
import com.example.FinanceManagementApp.repository.UsersRepo;
import com.example.FinanceManagementApp.security.CurrentUserPrincipal;
import com.example.FinanceManagementApp.service.UsersService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/users")
public class UsersController {
    @Autowired
    private UsersService usersService;


    @GetMapping("me")                                  //current user
    public ResponseEntity<UserResponse> getUserProfile(@AuthenticationPrincipal CurrentUserPrincipal principal) {

        UserResponse response = usersService.getUserProfile(principal);

        return  ResponseEntity.ok(response);
    }

    @PostMapping("me")
    public ResponseEntity<Void> updateUserProfile(@AuthenticationPrincipal CurrentUserPrincipal principal,@RequestBody @Valid UpdateUserRequest dto) {

        usersService.updateUserProfile(principal,dto);

        return ResponseEntity.noContent().build();

    }
}
