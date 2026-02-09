package com.example.FinanceManagementApp.service;



import com.example.FinanceManagementApp.model.entity.Users;
import com.example.FinanceManagementApp.security.CurrentUserPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    public Users getCurrentUser() {
        CurrentUserPrincipal principal =
                (CurrentUserPrincipal) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        return principal.getUser();


    }


}