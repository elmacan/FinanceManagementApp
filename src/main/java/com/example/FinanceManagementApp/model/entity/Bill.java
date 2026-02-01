package com.example.FinanceManagementApp.model.entity;

import com.example.FinanceManagementApp.model.enums.BillStatus;
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
    private BillStatus status;

    @ManyToOne
    private Users user;


}
