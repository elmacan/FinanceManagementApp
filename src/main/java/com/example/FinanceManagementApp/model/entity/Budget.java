package com.example.FinanceManagementApp.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "user_id",
                                "category_id",
                                "month",
                                "year"
                        }
                )
        }
)
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, precision=19, scale=4)
    private BigDecimal limitAmount;

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private Integer year;

    @ManyToOne
    private Category category;  //expenseCategory olcak

    @ManyToOne
    private Users user;
}
