package com.example.FinanceManagementApp.repository;

import com.example.FinanceManagementApp.model.entity.SavingGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavingGoalRepo extends JpaRepository<SavingGoal,Long> {


    Optional<SavingGoal> findByIdAndUser_Id(Long id, Long userId);

    List<SavingGoal> findByUser_Id(Long userId);
}
