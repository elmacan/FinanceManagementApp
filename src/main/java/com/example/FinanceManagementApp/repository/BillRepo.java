package com.example.FinanceManagementApp.repository;

import com.example.FinanceManagementApp.model.entity.Bill;
import com.example.FinanceManagementApp.model.entity.Users;
import com.example.FinanceManagementApp.model.enums.BillStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepo extends JpaRepository<Bill, Long> {

    boolean existsByUserAndNameAndDueDate(Users user, String name, LocalDate dueDate);

    Optional<Bill> findByIdAndUser_Id(Long id, Long userId);

    List<Bill> findByUser_IdOrderByDueDateAsc(Long userId);

    List<Bill> findByUser_IdAndStatusNotAndDueDateBefore(Long userId, BillStatus status, LocalDate dueDateBefore);

    List<Bill> findByUser_IdAndStatus(Long userId, BillStatus status);

    @Query("""
        select coalesce(sum(b.amount),0)
        from Bill b
        where b.user.id = :userId
        and b.status <> 'PAID'
    """)
    BigDecimal totalUnpaidAmount(Long userId);

    List<Bill> findByUser_Id(Long userId);
}
