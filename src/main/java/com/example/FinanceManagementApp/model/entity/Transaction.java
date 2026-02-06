package com.example.FinanceManagementApp.model.entity;

import com.example.FinanceManagementApp.model.enums.CurrencyType;
import com.example.FinanceManagementApp.model.enums.TransactionSourceType;
import com.example.FinanceManagementApp.model.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, precision=19, scale=4)
    private BigDecimal originalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CurrencyType originalCurrency;

    @Column(nullable=false, precision=19, scale=6)
    private BigDecimal rate;

    @Column(nullable=false, precision=19, scale=4)
    private BigDecimal convertedAmount;  //userın base currency sine göre xxx

    private CurrencyType convertedCurrency;  //= işlem anındaki user.baseCurrency   (user base değiştirir diye)

    //base currency ile yapılan işlemlerde rate = 1 ve convertedAmount = originalAmount set edilmeli

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    private TransactionSourceType sourceType;

    private Long sourceId;



    @Column(nullable = false)
    private LocalDateTime transactionDate;  //user input


    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt;
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @ManyToOne
    private Category category;


    @ManyToOne(optional=false)
    private Users user;

    private String description;


}

//amount için -> @Column(precision = 19, scale = 4)
//rate için -> @Column(precision = 19, scale = 6)