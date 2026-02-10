package com.example.FinanceManagementApp.service;

import com.example.FinanceManagementApp.dto.request.UpdateUserRequest;
import com.example.FinanceManagementApp.dto.response.UserResponse;
import com.example.FinanceManagementApp.security.CurrentUserPrincipal;
import jakarta.transaction.Transactional;

public interface UsersService {
    UserResponse getUserProfile(CurrentUserPrincipal principal);

    @Transactional
    void updateUserProfile(CurrentUserPrincipal principal, UpdateUserRequest dto);
}
