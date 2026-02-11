package com.example.FinanceManagementApp.model.entity;

import com.example.FinanceManagementApp.model.enums.CurrencyType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
public class Users {

    //buradaki validation kullanılmıyor , controller ile @Valid yapılmadığı sürece

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String userName;
    @Column(nullable = false, unique=true)
    private String email;

    @JsonIgnore
    @Column(nullable=false)
    private String password;

    @Column(precision=19, scale=4)
    private BigDecimal monthlyIncome =BigDecimal.ZERO;   //default=0


    @Column(nullable=false)
    @Enumerated(EnumType.STRING)
    private CurrencyType baseCurrency;






}
