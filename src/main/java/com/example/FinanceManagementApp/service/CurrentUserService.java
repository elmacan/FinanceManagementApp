package com.example.FinanceManagementApp.service;


import com.example.FinanceManagementApp.exception.ApiException;
import com.example.FinanceManagementApp.model.entity.Users;
import com.example.FinanceManagementApp.repository.UsersRepo;
import com.example.FinanceManagementApp.security.CurrentUserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    @Autowired
    private UsersRepo userRepo;

    public Users getCurrentUser(CurrentUserPrincipal principal) {

        return userRepo.findById(principal.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));


    }
}