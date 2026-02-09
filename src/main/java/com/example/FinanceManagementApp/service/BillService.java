package com.example.FinanceManagementApp.service;

import com.example.FinanceManagementApp.dto.request.BillRequest;
import com.example.FinanceManagementApp.dto.response.BillPayResponse;
import com.example.FinanceManagementApp.dto.response.BillResponse;
import com.example.FinanceManagementApp.exception.ApiException;
import com.example.FinanceManagementApp.model.entity.Bill;
import com.example.FinanceManagementApp.model.entity.Category;
import com.example.FinanceManagementApp.model.entity.Users;
import com.example.FinanceManagementApp.model.enums.BillStatus;
import com.example.FinanceManagementApp.model.enums.TransactionType;
import com.example.FinanceManagementApp.repository.BillRepo;
import com.example.FinanceManagementApp.repository.CategoryRepo;
import com.example.FinanceManagementApp.security.CurrentUserPrincipal;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BillService {

    @Autowired private BillRepo billRepo;
    @Autowired private CategoryRepo categoryRepo;
    @Autowired private TransactionService transactionService;


    @Transactional
    public BillResponse create(CurrentUserPrincipal principal, @Valid BillRequest dto) {
        Users user = principal.getUser();

        Category category = categoryRepo.findByIdAndUser_Id(dto.getCategoryId(), user.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Category not found"));

        if (category.getType() != TransactionType.EXPENSE) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Bill category must be EXPENSE");
        }

        if (billRepo.existsByUserAndNameAndDueDate(user, dto.getName(), dto.getDueDate())) {
            throw new ApiException(HttpStatus.CONFLICT, "Bill already exists");
        }

        Bill bill = new Bill();
        bill.setName(dto.getName());
        bill.setAmount(dto.getAmount());
        bill.setDueDate(dto.getDueDate());
        bill.setCategory(category);
        bill.setStatus(BillStatus.UNPAID);
        bill.setUser(user);


        return new BillResponse(billRepo.save(bill));


    }

    public BillResponse get(CurrentUserPrincipal principal, Long id) {
        Bill b = billRepo.findByIdAndUser_Id(id, principal.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Bill not found"));
        return new BillResponse(b);

    }

    public List<BillResponse> list(CurrentUserPrincipal principal, BillStatus status) {
        Long userId = principal.getId();
        LocalDate today= LocalDate.now();

        List<Bill> bills;
        //tüm faturalar
        if (status == null) {
            bills = billRepo.findByUser_IdOrderByDueDateAsc(userId);
        }
        else if (status == BillStatus.OVERDUE) {
        // dinamik overdue
            bills = billRepo.findByUser_IdAndStatusNotAndDueDateBefore(userId, BillStatus.PAID, today);
        }
        else {
        // PAID veya UNPAID
            bills = billRepo.findByUser_IdAndStatus(userId, status);
        }
        return bills
                .stream()
                .map(BillResponse::new)
                .toList();
    }


    @Transactional
    public BillPayResponse pay(Long billId, CurrentUserPrincipal principal) {
        Users user = principal.getUser();

        Bill bill = billRepo.findByIdAndUser_Id(billId, user.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Bill not found"));

        if (bill.getCategory().getType() != TransactionType.EXPENSE) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Bill category must be EXPENSE");
        }

        if (bill.getStatus() == BillStatus.PAID)
            throw new ApiException(HttpStatus.BAD_REQUEST, "Already paid");

        bill.setStatus(BillStatus.PAID);

        return new BillPayResponse(new BillResponse(bill),transactionService.createFromBill(bill,user));

    }
}
