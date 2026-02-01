package com.example.FinanceManagementApp.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

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

    @Column(nullable=false, precision=19, scale=4)
    private BigDecimal amount;

    private LocalDate plannedDate;

    @Column(nullable=false)
    private Boolean completed=false;

    @ManyToOne
    private Category category;

    @ManyToOne
    private Users user;
}
