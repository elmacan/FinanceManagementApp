package com.example.FinanceManagementApp.model.entity;

import com.example.FinanceManagementApp.model.enums.CurrencyType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames={
                                "user_id",
                                "title",
                                "currency"
                        }
                )
        }
)
public class SavingGoal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(nullable=false, precision=19, scale=4)
    private BigDecimal targetAmount;


    @Enumerated(EnumType.STRING)
    private CurrencyType currency;

    private LocalDate targetDate;

    @Column(nullable = false)
    private Boolean completed=false;

    @ManyToOne(optional = false)
    private Users user;

    @OneToMany(mappedBy = "goal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SavingTransaction> savingTransactionList = new ArrayList<>();

}
