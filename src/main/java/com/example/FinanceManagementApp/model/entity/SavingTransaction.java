package com.example.FinanceManagementApp.model.entity;

import com.example.FinanceManagementApp.model.enums.CurrencyType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
public class SavingTransaction {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable=false, precision=19, scale=4)
    private BigDecimal originalAmount;
    @Enumerated(EnumType.STRING)
    private CurrencyType originalCurrency;

    private LocalDate date;

    @Column(nullable=false, precision=19, scale=4)
    private BigDecimal convertedAmount;
    @Enumerated(EnumType.STRING)
    private CurrencyType goalCurrency;

    @Column(nullable=false, precision=19, scale=6)
    private BigDecimal rate;

    @ManyToOne
    private SavingGoal goal;



}
