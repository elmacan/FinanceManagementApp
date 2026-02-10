package com.example.FinanceManagementApp.service;

import com.example.FinanceManagementApp.dto.request.UpdateUserRequest;
import com.example.FinanceManagementApp.dto.response.UserResponse;
import com.example.FinanceManagementApp.model.entity.Users;
import com.example.FinanceManagementApp.repository.UsersRepo;
import com.example.FinanceManagementApp.security.CurrentUserPrincipal;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@RequiredArgsConstructor
@Service
public class UsersServiceImpl implements UsersService {

    private final UsersRepo usersRepo;


    @Override
    public UserResponse getUserProfile(CurrentUserPrincipal principal) {
        Users user=principal.getUser();
        return new UserResponse(user.getId(),user.getEmail(),user.getUserName(),user.getBaseCurrency(),user.getMonthlyIncome());

    }

    @Transactional
    @Override
    public void updateUserProfile(CurrentUserPrincipal principal, UpdateUserRequest dto) {
        Users user=principal.getUser();

        if(dto.getUserName()!=null){
            user.setUserName(dto.getUserName());
        }
        if(dto.getBaseCurrency()!=null){
            user.setBaseCurrency(dto.getBaseCurrency());
        }
        if(dto.getMonthlyIncome()!=null){
            user.setMonthlyIncome(normalize(dto.getMonthlyIncome()));
        }


        usersRepo.save(user);

    }

    //12.2      → 12.2000
    //12.23456  → 12.2346
    //4basamağa yuvarlama kısmı
    private BigDecimal normalize(BigDecimal value) {
        return value.setScale(4, RoundingMode.HALF_UP);
    }

    //user 2k girmek isterse 2.000 değil 2000 girmeli
}
