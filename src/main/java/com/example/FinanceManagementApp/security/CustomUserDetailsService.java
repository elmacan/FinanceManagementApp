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
                user.getEmail(),      // principal = email
                user.getPassword(),
                new ArrayList<>()
        );
    }
}

//“Kullanıcıyı nereden ve nasıl bulacağını” söyleyen servistir.
//to verify email and password
//login request-> auth manager -> auth provider -> userdetailsService -> UserRepo -> DB

//Spring Security’nin LOGIN sırasında kullandığı servistir.