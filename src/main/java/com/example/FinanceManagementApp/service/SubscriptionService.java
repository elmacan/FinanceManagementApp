package com.example.FinanceManagementApp.service;

import com.example.FinanceManagementApp.dto.request.SubscriptionRequest;
import com.example.FinanceManagementApp.dto.request.SubscriptionUpdateRequest;
import com.example.FinanceManagementApp.dto.response.SubscriptionResponse;
import com.example.FinanceManagementApp.exception.ApiException;
import com.example.FinanceManagementApp.model.entity.Category;
import com.example.FinanceManagementApp.model.entity.Subscription;
import com.example.FinanceManagementApp.model.entity.Users;
import com.example.FinanceManagementApp.model.enums.TransactionType;
import com.example.FinanceManagementApp.repository.CategoryRepo;
import com.example.FinanceManagementApp.repository.SubscriptionRepo;
import com.example.FinanceManagementApp.security.CurrentUserPrincipal;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SubscriptionService {
   @Autowired private SubscriptionRepo subscriptionRepo;
    @Autowired private CategoryRepo categoryRepo;
    @Autowired private TransactionService transactionService;



    @Transactional
    public SubscriptionResponse create(CurrentUserPrincipal principal, @Valid SubscriptionRequest dto) {
        Users user = principal.getUser();

        if (subscriptionRepo.existsByUser_IdAndNameIgnoreCase(user.getId(), dto.getName())) {
            throw new ApiException(HttpStatus.CONFLICT, "Subscription name already exists");
        }

        Category category = getExpenseCategory(user.getId(), dto.getCategoryId());


        Subscription s = new Subscription();
        s.setUser(user);
        s.setName(dto.getName());
        s.setCategory(category);
        s.setMonthlyAmount(dto.getMonthlyAmount());
        s.setStartDate(dto.getStartDate());
        s.setBillingDay(dto.getBillingDay());

        boolean active = dto.getActive() == null || dto.getActive();
        s.setActive(active);

        s.setActiveFrom(dto.getStartDate());

        Subscription saved = subscriptionRepo.save(s);

        if (saved.getActive()) {
            generateMissingSubscriptionTransactions(user);
        }

        return new SubscriptionResponse(saved);

    }


    public SubscriptionResponse get(CurrentUserPrincipal principal, Long id) {
        Long userId = principal.getId();
        Subscription s = subscriptionRepo.findByIdAndUser_Id(id, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Subscription not found"));
        return new SubscriptionResponse(s);
    }

    public List<SubscriptionResponse> list(CurrentUserPrincipal principal,Boolean active) {
        Long userId = principal.getId();

        List<Subscription> list = (active == null)
                ? subscriptionRepo.findByUser_Id(userId)
                : subscriptionRepo.findByUser_IdAndActive(userId, active);

        return list.stream().map(SubscriptionResponse::new).toList();
    }


    @Transactional
    public SubscriptionResponse update(CurrentUserPrincipal principal, Long id, @Valid SubscriptionUpdateRequest dto) {
        Users user = principal.getUser();

        Subscription s = getOwned(user.getId(), id);

        if (!s.getName().equalsIgnoreCase(dto.getName())
                && subscriptionRepo.existsByUser_IdAndNameIgnoreCase(user.getId(), dto.getName())) {
            throw new ApiException(HttpStatus.CONFLICT, "Subscription name already exists");
        }

        Category category = getExpenseCategory(user.getId(), dto.getCategoryId());

        s.setName(dto.getName().trim());
        s.setCategory(category);
        s.setMonthlyAmount(dto.getMonthlyAmount());
        s.setBillingDay(dto.getBillingDay());

        Subscription saved = subscriptionRepo.save(s);

        if (saved.getActive()) {
            generateMissingSubscriptionTransactions(user);
        }

        return new SubscriptionResponse(saved);
    }



    @Transactional
    public SubscriptionResponse activate(CurrentUserPrincipal principal, Long id) {
        Users user = principal.getUser();
        Subscription s = getOwned(user.getId(), id);

        s.setActive(true);
        s.setActiveFrom(LocalDate.now());

        Subscription saved = subscriptionRepo.save(s);

        generateMissingSubscriptionTransactions(user);

        return new SubscriptionResponse(saved);
    }

    @Transactional
    public SubscriptionResponse deactivate(CurrentUserPrincipal principal, Long id) {
        Users user = principal.getUser();

        Subscription s = getOwned(user.getId(), id);

        s.setActive(false);

        Subscription saved = subscriptionRepo.save(s);

        return new SubscriptionResponse(saved);
    }

    //catch-up
    @Transactional
    public void generateMissingSubscriptionTransactions(Users user) {
        List<Subscription> subs = subscriptionRepo.findByUser_IdAndActiveTrue(user.getId());

        LocalDate today = LocalDate.now();

        for (Subscription sub : subs) {


            LocalDate effectiveStart = sub.getActiveFrom().isAfter(sub.getStartDate())
                    ? sub.getActiveFrom()
                    : sub.getStartDate();

            LocalDate cursor = firstBillingDateOnOrAfter(effectiveStart, sub.getBillingDay());

            while (!cursor.isAfter(today)) {

                transactionService.createFromSubscription(sub, user, cursor);

                cursor = nextMonth(cursor, sub.getBillingDay());
            }
        }
    }





    private Subscription getOwned(Long userId, Long id) {
        return subscriptionRepo.findByIdAndUser_Id(id, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Subscription not found"));
    }

    private Category getExpenseCategory(Long userId, Long categoryId) {
        Category c = categoryRepo.findByIdAndUser_Id(categoryId, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Category not found"));

        if (c.getType() != TransactionType.EXPENSE) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Subscription category must be EXPENSE");
        }
        return c;
    }

    // billingDay 1-28
    private LocalDate firstBillingDateOnOrAfter(LocalDate start, int billingDay) {
        LocalDate d = start.withDayOfMonth(billingDay);
        if (d.isBefore(start)) {
            d = start.plusMonths(1).withDayOfMonth(billingDay);
        }
        return d;
    }

    private LocalDate nextMonth(LocalDate current, int billingDay) {
        return current.plusMonths(1).withDayOfMonth(billingDay);
    }
}
