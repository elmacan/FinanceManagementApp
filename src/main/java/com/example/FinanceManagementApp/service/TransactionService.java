package com.example.FinanceManagementApp.service;

import com.example.FinanceManagementApp.dto.request.BaseTransactionRequest;
import com.example.FinanceManagementApp.dto.request.ExpenseRequest;
import com.example.FinanceManagementApp.dto.request.IncomeRequest;
import com.example.FinanceManagementApp.dto.response.TransactionResponse;
import com.example.FinanceManagementApp.exception.ApiException;
import com.example.FinanceManagementApp.model.entity.Category;
import com.example.FinanceManagementApp.model.entity.Transaction;
import com.example.FinanceManagementApp.model.entity.Users;
import com.example.FinanceManagementApp.model.enums.CurrencyType;
import com.example.FinanceManagementApp.model.enums.TransactionSourceType;
import com.example.FinanceManagementApp.model.enums.TransactionType;
import com.example.FinanceManagementApp.repository.CategoryRepo;
import com.example.FinanceManagementApp.repository.TransactionRepo;
import com.example.FinanceManagementApp.security.CurrentUserPrincipal;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepo transactionRepo;
    @Autowired
    private CategoryRepo categoryRepo;
    @Autowired
    private CurrentUserService currentUserService;
    @Autowired
    private CurrencyService  currencyService;








    public TransactionResponse createExpense(CurrentUserPrincipal principal, @Valid ExpenseRequest dto) {
        Users user = currentUserService.getCurrentUser(principal);

        Category category = getUserCategory(user.getId(), dto.getCategoryId());
        ensureCategoryType(category, TransactionType.EXPENSE);

        Transaction tx=build(dto,user,category,TransactionType.EXPENSE);
        Transaction saved=transactionRepo.save(tx);
        String warning="budget için warning eklencek";

        return new TransactionResponse(saved,warning);


    }

    public TransactionResponse createIncome(CurrentUserPrincipal principal, @Valid IncomeRequest dto) {
        Users user= currentUserService.getCurrentUser(principal);

        Category category=null;
        //category optional income için

        if(dto.getCategoryId()!=null){
            category=getUserCategory(user.getId(), dto.getCategoryId());
            ensureCategoryType(category,TransactionType.INCOME);
        }

        Transaction tx=build(dto,user,category,TransactionType.INCOME);
        Transaction saved=transactionRepo.save(tx);

        return new TransactionResponse(saved,null);

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


    private Transaction build(BaseTransactionRequest dto, Users user, Category category, TransactionType type) {


       CurrencyType from= dto.getCurrency();
       CurrencyType to=user.getBaseCurrency();

        BigDecimal rate=currencyService.getRate(from,to);
        BigDecimal converted=currencyService.convert(dto.getAmount(),from,to);

       Transaction tx = new Transaction();
       tx.setUser(user);
       tx.setType(TransactionType.INCOME);

       tx.setCategory(category);
       tx.setTransactionDate(dto.getTransactionDate());
       tx.setDescription(dto.getDescription());

       tx.setOriginalAmount(dto.getAmount());
       tx.setOriginalCurrency(from);

       tx.setRate(rate);
       tx.setConvertedAmount(converted);
       tx.setConvertedCurrency(to);

       tx.setType(type);
       tx.setSourceType(TransactionSourceType.MANUAL);
       tx.setSourceId(null);

        return tx;
    }


    //mapping builder ile
    /*private TransactionResponse toResponse(Transaction tx,String warning) {

        Category category = tx.getCategory();

        return TransactionResponse.builder()
                .id(tx.getId())
                .originalAmount(tx.getOriginalAmount())
                .originalCurrency(tx.getOriginalCurrency())

                .convertedAmount(tx.getConvertedAmount())
                .convertedCurrency(tx.getConvertedCurrency())

                .rate(tx.getRate())

                .type(tx.getType())
                .transactionDate(tx.getTransactionDate())
                .description(tx.getDescription())

                .categoryId(category != null ? category.getId() : null)
                .categoryName(category != null ? category.getName() : null)

                .sourceType(tx.getSourceType())
                .sourceId(tx.getSourceId())

                .warning(warning)
                .build();
    }*/
}
