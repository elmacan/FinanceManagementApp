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
                        columnNames={"user_id","name"}
                )
        }
) //aynı user aynı isimde aboneliği iki kere aktif edemez
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(nullable=false, precision=19, scale=4)
    private BigDecimal monthlyAmount;

    private
    LocalDate startDate;

    private Integer billingDay; // Örn: her ay 15'i

    private Boolean active;

    @ManyToOne
    private Users users;

    private Boolean autoPay;
}
