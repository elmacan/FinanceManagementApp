package com.example.FinanceManagementApp.model.entity;

import com.example.FinanceManagementApp.model.enums.CurrencyType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
@Entity
public class Users {

    //buradaki validation kullanılmıyor , controller ile @Valid yapılmadığı sürece

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userName;
    @Column(unique=true)
    private String email;
    private String password;

    @Column(precision=19, scale=4)
    private BigDecimal monthlyIncome =BigDecimal.ZERO;;   //default=0

    @Enumerated(EnumType.STRING)
    private CurrencyType baseCurrency;






}
