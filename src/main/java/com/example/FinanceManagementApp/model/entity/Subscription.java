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
                        columnNames={"user_id","name"}
                )
        }
) //aynı user aynı isimde aboneliği iki kere aktif edemez
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(optional = false)
    private Category category;


    @Column(nullable=false, precision=19, scale=4)
    private BigDecimal monthlyAmount;

    private
    LocalDate startDate;

    private Integer billingDay; // Örn: her ay 15'i

    @Column(nullable=false)
    private Boolean active;

    @ManyToOne(optional = false)
    private Users user;

    private Boolean autoPay;

    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt;
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
