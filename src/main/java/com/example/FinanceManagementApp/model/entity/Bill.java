package com.example.FinanceManagementApp.model.entity;

import com.example.FinanceManagementApp.model.enums.BillStatus;
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
                                "name",
                                "due_date"
                        }
                )
        }
)
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(nullable=false, precision=19, scale=4)
    private BigDecimal amount;

    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private BillStatus status;

    @ManyToOne(optional = false)
    private Users user;

    @Column(nullable=false, updatable=false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }



}
