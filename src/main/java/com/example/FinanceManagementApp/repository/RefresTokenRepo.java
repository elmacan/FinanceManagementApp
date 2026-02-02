package com.example.FinanceManagementApp.repository;

import com.example.FinanceManagementApp.model.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefresTokenRepo extends JpaRepository<RefreshToken,Long> {
}
