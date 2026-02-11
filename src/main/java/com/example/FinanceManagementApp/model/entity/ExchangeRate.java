package com.example.FinanceManagementApp.model.entity;

import com.example.FinanceManagementApp.model.enums.CurrencyType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(uniqueConstraints=@UniqueConstraint(
                columnNames={"currency","rateDate"}))
@Data
public class ExchangeRate {

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private CurrencyType currency;

    @Column(precision=19, scale=6)
    private BigDecimal tryRate;   // ForexBuying

    private LocalDate rateDate;

    private String source; // TCMB
}

//USD     43.12       2026-02-12      TCMB
//EUR     50.80       2026-02-12      TCMB

