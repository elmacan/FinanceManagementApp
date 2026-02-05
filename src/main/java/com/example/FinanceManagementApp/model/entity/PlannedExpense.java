package com.example.FinanceManagementApp.model.entity;

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

    @Column(nullable=false, precision=19, scale=4)
    private BigDecimal amount;

    private LocalDate plannedDate;

    @Column(nullable=false)
    private Boolean completed=false;

    @ManyToOne
    private Category category;

    @ManyToOne(optional = false)
    private Users user;

    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt;
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
