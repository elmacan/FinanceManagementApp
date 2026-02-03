package com.example.FinanceManagementApp.repository;

import com.example.FinanceManagementApp.model.entity.RefreshToken;
import com.example.FinanceManagementApp.model.entity.Users;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepo extends JpaRepository<RefreshToken,Long> {

    Optional<RefreshToken> findByToken(String token);
    void deleteByUser_Id(Long userId);


}
