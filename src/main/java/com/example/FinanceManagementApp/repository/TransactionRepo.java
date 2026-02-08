package com.example.FinanceManagementApp.repository;

import com.example.FinanceManagementApp.model.entity.Category;
import com.example.FinanceManagementApp.model.entity.Transaction;
import com.example.FinanceManagementApp.model.entity.Users;
import com.example.FinanceManagementApp.model.enums.TransactionSourceType;
import com.example.FinanceManagementApp.model.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; //kullanılabilir
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction, Long>{

    Optional<Transaction> findByIdAndUser_Id(Long id, Long userId);

    @Query("""
        SELECT t FROM Transaction t
        WHERE t.user.id = :userId
          AND (:month IS NULL OR t.month = :month)
          AND (:year IS NULL OR t.year = :year)
          AND (:type IS NULL OR t.type = :type)
          AND (:categoryId IS NULL OR t.category.id = :categoryId)
          AND (:sourceType IS NULL OR t.sourceType = :sourceType)
          AND (:from IS NULL OR t.transactionDate >= :from)
          AND (:to IS NULL OR t.transactionDate <= :to)
        ORDER BY t.transactionDate DESC, t.createdAt DESC
    """)
    List<Transaction> filter(
            @Param("userId") Long userId,
            @Param("month") Integer month,
            @Param("year") Integer year,
            @Param("type") TransactionType type,
            @Param("categoryId") Long categoryId,
            @Param("sourceType") TransactionSourceType sourceType,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );


    @Query("""
        select coalesce(sum(t.convertedAmount),0)
        from Transaction t
        where t.user = :user
        and t.category = :category
        and t.month = :month
        and t.year = :year
        and t.type = 'EXPENSE'
        """)
    BigDecimal sumExpenseForBudget(Users user, Category category, Integer month, Integer year);
}
