package com.example.FinanceManagementApp.service.impl;

import com.example.FinanceManagementApp.dto.request.BaseTransactionRequest;
import com.example.FinanceManagementApp.dto.request.ExpenseRequest;
import com.example.FinanceManagementApp.dto.request.IncomeRequest;
import com.example.FinanceManagementApp.dto.response.BudgetWarningResponse;
import com.example.FinanceManagementApp.dto.response.TransactionResponse;
import com.example.FinanceManagementApp.exception.ApiException;
import com.example.FinanceManagementApp.model.entity.*;
import com.example.FinanceManagementApp.model.enums.CurrencyType;
import com.example.FinanceManagementApp.model.enums.TransactionSourceType;
import com.example.FinanceManagementApp.model.enums.TransactionType;
import com.example.FinanceManagementApp.repository.CategoryRepo;
import com.example.FinanceManagementApp.repository.TransactionRepo;
import com.example.FinanceManagementApp.security.CurrentUserPrincipal;
import com.example.FinanceManagementApp.service.BudgetService;
import com.example.FinanceManagementApp.service.TransactionService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {


    private final TransactionRepo transactionRepo;

    private final CategoryRepo categoryRepo;

    private final CurrencyServiceImpl currencyService;

    private final BudgetService budgetService;







    @Transactional
    @Override
    public TransactionResponse createExpense(CurrentUserPrincipal principal, @Valid ExpenseRequest dto) {
        Users user = principal.getUser();

        Category category = getUserCategory(user.getId(), dto.getCategoryId());
        ensureCategoryType(category, TransactionType.EXPENSE);

        Transaction tx=build(dto,user,category,TransactionType.EXPENSE);

        List<BudgetWarningResponse> warnings = computeWarningsBeforeSave(tx);

        Transaction saved=transactionRepo.save(tx);

        return new TransactionResponse(saved, warnings);


    }

    @Transactional
    @Override
    public TransactionResponse createIncome(CurrentUserPrincipal principal, @Valid IncomeRequest dto) {
        Users user= principal.getUser();

        Category category=null;
        //category optional income için

        if(dto.getCategoryId()!=null){
            category=getUserCategory(user.getId(), dto.getCategoryId());
            ensureCategoryType(category,TransactionType.INCOME);
        }

        Transaction tx=build(dto,user,category,TransactionType.INCOME);
        Transaction saved=transactionRepo.save(tx);

        return new TransactionResponse(saved, List.of());

    }

    @Override
    public TransactionResponse get(CurrentUserPrincipal principal, Long id) {

        Transaction tx=transactionRepo.findByIdAndUser_Id(id, principal.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Transaction not found"));

        return new TransactionResponse(tx, List.of());
    }


    @Override
    public List<TransactionResponse> list(
            CurrentUserPrincipal principal,
            Integer month,
            Integer year,
            TransactionType type,
            Long categoryId,
            TransactionSourceType sourceType,
            LocalDate from,
            LocalDate to
    ) {
        return transactionRepo.filter(
                principal.getId(),
                month,
                year,
                type,
                categoryId,
                sourceType,
                from,
                to
        ).stream().map(tx -> new TransactionResponse(tx, List.of())).toList();
    }

    //create from methodları düzenlencek ortak yapı ile

    @Transactional
    @Override
    public TransactionResponse createFromBill(Bill bill, Users user) {
        Category category = bill.getCategory();

        if (category.getType() != TransactionType.EXPENSE) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Bill category must be EXPENSE");
        }

        boolean exists = transactionRepo
                .existsByUser_IdAndSourceTypeAndSourceId(
                        user.getId(),
                        TransactionSourceType.BILL,
                        bill.getId()
                );

        if (exists) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "Transaction already created for this bill");
        }

        ExpenseRequest dto=new ExpenseRequest();
        dto.setAmount(bill.getAmount());
        dto.setCurrency(user.getBaseCurrency()); // bill currency yok
        dto.setTransactionDate(LocalDate.now());
        dto.setCategoryId(category.getId());
        dto.setDescription("Bill: " + bill.getName());

        Transaction tx=build(dto,user,category,TransactionType.EXPENSE);

        tx.setSourceType(TransactionSourceType.BILL);
        tx.setSourceId(bill.getId());

        List<BudgetWarningResponse> warnings = computeWarningsBeforeSave(tx);
        Transaction saved = transactionRepo.save(tx);

        return new TransactionResponse(saved, warnings);

    }


    @Transactional
    @Override
    public void createFromSubscription(
            Subscription sub,
            Users user,
            LocalDate date
    ) {
        Category category = sub.getCategory();

        boolean exists =
                transactionRepo.existsByUser_IdAndSourceTypeAndSourceIdAndMonthAndYear(
                        user.getId(),
                        TransactionSourceType.SUBSCRIPTION,
                        sub.getId(),
                        date.getMonthValue(),
                        date.getYear()
                );

        if (exists) return;

        ExpenseRequest dto = new ExpenseRequest();
        dto.setAmount(sub.getMonthlyAmount());
        dto.setCurrency(user.getBaseCurrency());
        dto.setTransactionDate(date);
        dto.setCategoryId(category.getId());
        dto.setDescription("Subscription: " + sub.getName());

        Transaction tx = build(dto, user, category, TransactionType.EXPENSE);

        tx.setSourceType(TransactionSourceType.SUBSCRIPTION);
        tx.setSourceId(sub.getId());

        transactionRepo.save(tx);


    }


    //KONTROL
    @Transactional
    @Override
    public TransactionResponse createFromPlannedExpense(PlannedExpense pe, Users user){

        Category category = pe.getCategory();

        if (category.getType() != TransactionType.EXPENSE) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "PlannedExpense category must be EXPENSE");
        }

        boolean exists = transactionRepo.existsByUser_IdAndSourceTypeAndSourceId(
                user.getId(),
                TransactionSourceType.PLANNED_EXPENSE,
                pe.getId()
        );

        if (exists) {
            throw new ApiException(HttpStatus.CONFLICT, "Transaction already created for this planned expense");
        }

        ExpenseRequest dto = new ExpenseRequest();
        dto.setAmount(pe.getOriginalAmount());
        dto.setCurrency(pe.getOriginalCurrency());
        dto.setTransactionDate(pe.getPlannedDate());
        dto.setCategoryId(category.getId());
        dto.setDescription("PlannedExpense: " + pe.getTitle());

        Transaction tx = build(dto, user, category, TransactionType.EXPENSE);
        tx.setSourceType(TransactionSourceType.PLANNED_EXPENSE);
        tx.setSourceId(pe.getId());

        List<BudgetWarningResponse> warnings = computeWarningsBeforeSave(tx);
        Transaction saved = transactionRepo.save(tx);

        return new TransactionResponse(saved, warnings);

    }




    private Category getUserCategory(Long userId, Long categoryId) {
        if (categoryId == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Category required");
        }

        return categoryRepo.findByIdAndUser_Id(categoryId, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Category not found"));
         }

    private void ensureCategoryType(Category category, TransactionType expected) {
        if (category.getType() != expected) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Transaction type mismatch. Expected: " + expected + ", category Type: " + category.getType());
        }
    }


    private Transaction build(BaseTransactionRequest dto, Users user, Category category, TransactionType type) {


       CurrencyType from= dto.getCurrency();
       CurrencyType to=user.getBaseCurrency();

        BigDecimal rate=currencyService.getRate(from,to);
        BigDecimal converted=currencyService.convert(dto.getAmount(),from,to);

       Transaction tx = new Transaction();
       tx.setUser(user);

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

        return tx;
    }

    private List<BudgetWarningResponse> computeWarningsBeforeSave(Transaction tx) {
        if (tx.getType() != TransactionType.EXPENSE || tx.getCategory() == null) {
            return List.of();
        }


        int month = tx.getTransactionDate().getMonthValue();
        int year = tx.getTransactionDate().getYear();

        return budgetService.checkExpenseAndWarnings(
                tx.getUser(),
                tx.getCategory().getId(),
                tx.getCategory().getName(),
                tx.getConvertedAmount(),
                month,
                year
        );
    }

}
