package com.example.FinanceManagementApp.repository;

import com.example.FinanceManagementApp.model.entity.Budget;
import com.example.FinanceManagementApp.model.entity.Category;
import com.example.FinanceManagementApp.model.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepo extends JpaRepository<Budget, Long> {
    boolean existsByUserAndCategoryAndMonthAndYear(Users user, Category category, Integer month, Integer year);

    List<Budget> findByUserAndMonthAndYear(Users user, Integer month, Integer year);

    Optional<Budget> findByIdAndUser_Id(Long id, Long userId);


    List<Budget> findByUser(Users user);
}
