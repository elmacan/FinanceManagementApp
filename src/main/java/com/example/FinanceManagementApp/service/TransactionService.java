package com.example.FinanceManagementApp.service;

import com.example.FinanceManagementApp.dto.request.ExpenseRequest;
import com.example.FinanceManagementApp.dto.request.IncomeRequest;
import com.example.FinanceManagementApp.dto.response.TransactionResponse;
import com.example.FinanceManagementApp.exception.ApiException;
import com.example.FinanceManagementApp.model.entity.Category;
import com.example.FinanceManagementApp.model.entity.Transaction;
import com.example.FinanceManagementApp.model.entity.Users;
import com.example.FinanceManagementApp.model.enums.TransactionSourceType;
import com.example.FinanceManagementApp.model.enums.TransactionType;
import com.example.FinanceManagementApp.repository.CategoryRepo;
import com.example.FinanceManagementApp.repository.TransactionRepo;
import com.example.FinanceManagementApp.security.CurrentUserPrincipal;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    TransactionRepo transactionRepo;
    @Autowired
    CategoryRepo categoryRepo;
    @Autowired
    private CurrentUserService currentUserService;








    public TransactionResponse createExpense(CurrentUserPrincipal principal, @Valid ExpenseRequest dto) {
    }

    public TransactionResponse createIncome(CurrentUserPrincipal principal, @Valid IncomeRequest dto) {
        Users user= currentUserService.getCurrentUser(principal);

        Category category=null;

        if(dto.getCategoryId()!=null){
            category=getUserCategory(user.getId(), dto.getCategoryId());
            ensureCategoryType(category,TransactionType.INCOME);
        }




    }

    public Transaction get(CurrentUserPrincipal principal, Long id) {
        return transactionRepo.findByIdAndUser_Id(id, principal.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Transaction not found"));
    }

    public List<Transaction> list(CurrentUserPrincipal principal, Integer month, Integer year, TransactionType type, Long categoryId, TransactionSourceType sourceType, LocalDate from, LocalDate to) {
    }






    private Category getUserCategory(Long userId, Long categoryId) {
        return categoryRepo.findByIdAndUser_Id(categoryId, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Category not found"));
    }

    private void ensureCategoryType(Category category, TransactionType expected) {
        if (category.getType() != expected) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Transaction type mismatch. Expected: " + expected);
        }
    }

    //inherit yapcam
    private Transaction build( IncomeRequest dto,
                               Users user,
                               Category category,
                               TransactionType type,
                               TransactionSourceType sourceType,
                               Long sourceId){

       Transaction tx = new Transaction();
       tx.setUser(user);



    }
}
