package com.example.FinanceManagementApp.model.entity;

import com.example.FinanceManagementApp.model.enums.CurrencyType;
import com.example.FinanceManagementApp.model.enums.TransactionSourceType;
import com.example.FinanceManagementApp.model.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false)
    private Users user;

    @ManyToOne
    private Category category;

    @Column(nullable=false, precision=19, scale=4)
    private BigDecimal originalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CurrencyType originalCurrency;

    @Column(nullable=false, precision=19, scale=6)
    private BigDecimal rate;

    @Column(nullable=false, precision=19, scale=4)
    private BigDecimal convertedAmount;  //userın base currency sine göre xxx

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CurrencyType convertedCurrency;  //= işlem anındaki user.baseCurrency   (user base değiştirir diye)

    //base currency ile yapılan işlemlerde rate = 1 ve convertedAmount = originalAmount set edilmeli

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionSourceType sourceType;

    private Long sourceId;

    @Column(nullable = false)
    private LocalDate transactionDate;  //user input


    //normalde türetilmiş veri saklanmaz
    //rapor vs için sürekli sorgu gerekcek
    @Column(nullable=false)
    private Integer month;

    @Column(nullable=false)
    private Integer year;

    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt;

    private String description;

    @PrePersist
    public void onCreate() {

        this.createdAt = LocalDateTime.now();
        this.month = transactionDate.getMonthValue();
        this.year = transactionDate.getYear();

    }






}

//amount için -> @Column(precision = 19, scale = 4)
//rate için -> @Column(precision = 19, scale = 6)