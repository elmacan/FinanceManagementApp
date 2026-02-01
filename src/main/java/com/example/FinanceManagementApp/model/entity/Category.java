package com.example.FinanceManagementApp.model.entity;

import com.example.FinanceManagementApp.model.enums.CategoryType;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"user_id", "name", "type"}
                )
        }
) //market expense,income olmasın aynı anda diye
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private CategoryType type;

    @ManyToOne
    private Users user;
}
