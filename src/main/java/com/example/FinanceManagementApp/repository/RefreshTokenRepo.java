package com.example.FinanceManagementApp.repository;

import com.example.FinanceManagementApp.model.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepo extends JpaRepository<RefreshToken,Long> {
}
