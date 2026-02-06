package com.example.FinanceManagementApp.repository;

import com.example.FinanceManagementApp.model.entity.Category;
import com.example.FinanceManagementApp.model.entity.Users;
import com.example.FinanceManagementApp.model.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepo extends JpaRepository<Category, Long> {




    List<Category> findByUserId(Long userId);

    boolean existsByUserIdAndNameAndType(Long userId, String name, TransactionType type);

    List<Category> findByUserIdAndType(Long userId, TransactionType type);

}
