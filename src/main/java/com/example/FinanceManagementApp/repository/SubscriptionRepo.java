package com.example.FinanceManagementApp.repository;

import com.example.FinanceManagementApp.dto.response.SubscriptionResponse;
import com.example.FinanceManagementApp.model.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepo extends JpaRepository<Subscription, Long> {


    boolean existsByUser_IdAndNameIgnoreCase(Long userId, String name);

    Optional<Subscription> findByIdAndUser_Id(Long id, Long userId);



    List<Subscription> findByUser_Id(Long userId);

    List<Subscription> findByUser_IdAndActive(Long userId, Boolean active);

    List<Subscription> findByUser_IdAndActiveTrue(Long userId);
}
