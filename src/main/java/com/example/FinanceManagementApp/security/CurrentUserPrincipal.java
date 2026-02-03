package com.example.FinanceManagementApp.security;

//Principal = Authentication Subject
//auth olmuş user

import lombok.Getter;

@Getter
public class CurrentUserPrincipal {
    private final Long id;
    private final String email;

    public CurrentUserPrincipal(Long id, String email) {
        this.id = id;
        this.email = email;
    }


}
