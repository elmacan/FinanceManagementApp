package com.example.FinanceManagementApp.controller;

import com.example.FinanceManagementApp.dto.request.UpdateUserRequest;
import com.example.FinanceManagementApp.dto.response.UserResponse;
import com.example.FinanceManagementApp.security.CurrentUserPrincipal;
import com.example.FinanceManagementApp.service.UsersServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
@RequiredArgsConstructor
@RestController
@RequestMapping("api/users")
public class UsersController {

    private final UsersServiceImpl usersService;


    @GetMapping("me")                                  //current user
    public ResponseEntity<UserResponse> getUserProfile(@AuthenticationPrincipal CurrentUserPrincipal principal) {

        UserResponse response = usersService.getUserProfile(principal);

        return  ResponseEntity.ok(response);
    }

    @PatchMapping("me")
    public ResponseEntity<Void> updateUserProfile(@AuthenticationPrincipal CurrentUserPrincipal principal,@RequestBody @Valid UpdateUserRequest dto) {

        usersService.updateUserProfile(principal,dto);

        return ResponseEntity.noContent().build();

    }
}
