package com.example.FinanceManagementApp.security;

import com.example.FinanceManagementApp.model.entity.Users;
import com.example.FinanceManagementApp.repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {


    @Autowired
    private UsersRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        Users user = userRepo
                .findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found")
                );

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),      // principal = EMAIL
                user.getPassword(),
                new ArrayList<>()
        );
    }


}
