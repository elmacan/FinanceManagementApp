package com.example.FinanceManagementApp.service;

import com.example.FinanceManagementApp.dto.request.CategoryRequest;
import com.example.FinanceManagementApp.dto.response.CategoryResponse;
import com.example.FinanceManagementApp.model.entity.Category;
import com.example.FinanceManagementApp.model.enums.TransactionType;
import com.example.FinanceManagementApp.security.CurrentUserPrincipal;
import jakarta.validation.Valid;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getCategories(CurrentUserPrincipal principal, TransactionType type);

    Category create(@Valid CategoryRequest dto, CurrentUserPrincipal principal);

    Category get(Long id, CurrentUserPrincipal principal);

    Category update(Long id, @Valid CategoryRequest dto, CurrentUserPrincipal principal);
}
