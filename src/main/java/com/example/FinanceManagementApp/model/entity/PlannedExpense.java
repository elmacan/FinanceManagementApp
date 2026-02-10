package com.example.FinanceManagementApp.model.entity;

import com.example.FinanceManagementApp.model.enums.CurrencyType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames={
                                "user_id",
                                "title",
                                "planned_date"
                        }
                )
        }
)
public class PlannedExpense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @ManyToOne(optional = false)
    private Category category;

    @Column(nullable=false, precision=19, scale=4)
    private BigDecimal originalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private CurrencyType originalCurrency;

    //planned aşamada da budget var ondan
    //user base currency
    @Column(nullable=false, precision=19, scale=4)
    private BigDecimal convertedAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private CurrencyType convertedCurrency;

    @Column(nullable=false, precision=19, scale=6)
    private BigDecimal rate;


    private LocalDate plannedDate;

    @Column(nullable=false)
    private Boolean completed=false;

   // private LocalDate completedAt;

    @ManyToOne(optional = false)
    private Users user;

    private String description;

    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt;
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
