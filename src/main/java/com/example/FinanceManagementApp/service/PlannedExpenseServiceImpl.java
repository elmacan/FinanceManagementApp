package com.example.FinanceManagementApp.service;

import com.example.FinanceManagementApp.currency.CurrencyService;
import com.example.FinanceManagementApp.dto.request.PlannedExpenseRequest;
import com.example.FinanceManagementApp.dto.response.PlannedExpenseResponse;
import com.example.FinanceManagementApp.dto.response.TransactionResponse;
import com.example.FinanceManagementApp.exception.ApiException;
import com.example.FinanceManagementApp.model.entity.Category;
import com.example.FinanceManagementApp.model.entity.PlannedExpense;
import com.example.FinanceManagementApp.model.entity.Users;
import com.example.FinanceManagementApp.model.enums.CurrencyType;
import com.example.FinanceManagementApp.model.enums.TransactionType;
import com.example.FinanceManagementApp.repository.CategoryRepo;
import com.example.FinanceManagementApp.repository.PlannedExpenseRepo;
import com.example.FinanceManagementApp.security.CurrentUserPrincipal;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlannedExpenseServiceImpl implements PlannedExpenseService {
    private final PlannedExpenseRepo plannedExpenseRepo;
    private final CategoryRepo categoryRepo;
    private final TransactionServiceImpl transactionService;
    private final CurrencyService currencyService;


    @Transactional
    @Override
    public PlannedExpense create(CurrentUserPrincipal principal, @Valid PlannedExpenseRequest dto) {
        Users user = principal.getUser();
        Category category = categoryRepo.findByIdAndUser_Id(dto.getCategoryId(), user.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Category not found"));

        if (category.getType() != TransactionType.EXPENSE) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "PlannedExpense category must be EXPENSE");
        }

        CurrencyType from = dto.getCurrency();
        CurrencyType to = user.getBaseCurrency();
        BigDecimal rate = currencyService.getRate(from, to);
        BigDecimal converted = currencyService.convert(dto.getAmount(), from, to);


        PlannedExpense pe = new PlannedExpense();
        pe.setTitle(dto.getTitle());
        pe.setCategory(category);

        pe.setOriginalAmount(dto.getAmount());
        pe.setOriginalCurrency(from);
        pe.setConvertedAmount(converted);
        pe.setConvertedCurrency(to);
        pe.setRate(rate);

        pe.setPlannedDate(dto.getPlannedDate());
        pe.setCompleted(false);
        pe.setUser(user);
        pe.setDescription(dto.getDescription());

        return plannedExpenseRepo.save(pe);
    }



    @Override
    public PlannedExpense get(CurrentUserPrincipal principal, Long id) {
        return plannedExpenseRepo.findByIdAndUser_Id(id, principal.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "PlannedExpense not found"));
    }


    @Override
    public List<PlannedExpenseResponse> listAll(CurrentUserPrincipal principal) {
        List<PlannedExpense> list =
                plannedExpenseRepo.findAllByUser_Id(principal.getId());

        return list.stream()
                .map(PlannedExpenseResponse::new)
                .toList();

    }


    @Transactional
    @Override
    public TransactionResponse complete(CurrentUserPrincipal principal, Long id) {
        PlannedExpense pe = plannedExpenseRepo
                .findByIdAndUser_Id(id, principal.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "PlannedExpense not found"));

        if (pe.getCompleted()) {
            throw new ApiException(HttpStatus.CONFLICT, "Already completed");
        }
        TransactionResponse tx = transactionService.createFromPlannedExpense(pe, principal.getUser());
        pe.setCompleted(true);
        plannedExpenseRepo.save(pe);
        return tx;

    }

    @Override
    public PlannedExpense updatePlannedExpense(Long id, @Valid PlannedExpenseRequest dto, CurrentUserPrincipal principal) {
            PlannedExpense pe = plannedExpenseRepo
                .findByIdAndUser_Id(id, principal.getId())
                .orElseThrow(() ->
                        new ApiException(HttpStatus.NOT_FOUND, "PlannedExpense not found"));


        if (pe.getCompleted()) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "Completed planned expense cannot be updated"
            );
        }
        if (dto.getDescription() != null) {
            pe.setDescription(dto.getDescription());
        }

        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            pe.setTitle(dto.getTitle());
        }

        if (dto.getAmount() != null || dto.getCurrency() != null) {

            BigDecimal amount = dto.getAmount() != null
                    ? dto.getAmount()
                    : pe.getOriginalAmount();

            CurrencyType from = dto.getCurrency() != null
                    ? dto.getCurrency()
                    : pe.getOriginalCurrency();

            CurrencyType to = pe.getUser().getBaseCurrency();

            BigDecimal rate = currencyService.getRate(from, to);
            BigDecimal converted = currencyService.convert(amount, from, to);

            pe.setOriginalAmount(amount);
            pe.setOriginalCurrency(from);
            pe.setConvertedAmount(converted);
            pe.setConvertedCurrency(to);
            pe.setRate(rate);
        }

        if (dto.getPlannedDate() != null) {
            pe.setPlannedDate(dto.getPlannedDate());
        }

        if (dto.getCategoryId() != null) {

            Category category = categoryRepo
                    .findByIdAndUser_Id(dto.getCategoryId(), principal.getId())
                    .orElseThrow(() ->
                            new ApiException(HttpStatus.NOT_FOUND, "Category not found"));

            if (category.getType() != TransactionType.EXPENSE) {
                throw new ApiException(
                        HttpStatus.BAD_REQUEST,
                        "PlannedExpense category must be EXPENSE"
                );
            }

            pe.setCategory(category);
        }

        return plannedExpenseRepo.save(pe);

    }
}
