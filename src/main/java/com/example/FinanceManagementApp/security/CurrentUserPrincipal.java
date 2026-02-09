package com.example.FinanceManagementApp.security;

//Principal = Authentication Subject
//auth olmuş user

import com.example.FinanceManagementApp.model.entity.Users;
import lombok.*;


//auth dan gelen
@Data
@AllArgsConstructor
public class CurrentUserPrincipal {
    private final Long id;
    private final String email;
    private final Users user;


    public CurrentUserPrincipal(Users user) {
        this.id=user.getId();
        this.email=user.getEmail();
        this.user = user;
    }

}
