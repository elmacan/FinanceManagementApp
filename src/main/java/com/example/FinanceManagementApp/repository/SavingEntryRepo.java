package com.example.FinanceManagementApp.repository;

import com.example.FinanceManagementApp.model.entity.SavingEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SavingEntryRepo extends JpaRepository<SavingEntry,Long> {

    List<SavingEntry> findByGoal_IdAndGoal_User_Id(Long goalId, Long userId);


    // Progress toplamı (goal currency cinsinden)
    @Query("""
        select coalesce(sum(e.convertedAmount), 0)
        from SavingEntry e
        where e.goal.id = :goalId
          and e.goal.user.id = :userId
    """)
    BigDecimal sumProgress(Long goalId, Long userId);


    // Goal için entry var mı
    boolean existsByGoal_IdAndGoal_User_Id(Long goalId, Long userId);


    // Goal içindeki entry sayısı
    long countByGoal_IdAndGoal_User_Id(Long goalId, Long userId);



}
