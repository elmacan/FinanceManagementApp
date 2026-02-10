package com.example.FinanceManagementApp.repository;

import com.example.FinanceManagementApp.model.entity.Category;
import com.example.FinanceManagementApp.model.entity.PlannedExpense;
import com.example.FinanceManagementApp.model.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlannedExpenseRepo extends JpaRepository<PlannedExpense, Long> {

    Optional<PlannedExpense> findByIdAndUser_Id(Long id, Long userId);

    List<PlannedExpense> findAllByUser_Id(Long userId);

    @Query("""
    select coalesce(sum(pe.convertedAmount),0)
    from PlannedExpense pe
    where pe.user = :user
      and (:category is null or pe.category = :category)
      and pe.completed = false
      and function('month', pe.plannedDate) = :month
      and function('year', pe.plannedDate) = :year
""")
    BigDecimal sumPlannedForBudget(Users user, Category category, Integer month, Integer year);

}
