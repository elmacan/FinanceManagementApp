package com.example.FinanceManagementApp.model.entity;

import com.example.FinanceManagementApp.model.enums.CurrencyType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @Column(unique=true)
    private String email;
    private String password;

    @Column(precision=19, scale=4)
    private BigDecimal monthlyIncome;   //eğer yoksa

    private CurrencyType baseCurrency;
}
