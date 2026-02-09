package com.example.FinanceManagementApp.service;

import com.example.FinanceManagementApp.dto.request.CategoryRequest;
import com.example.FinanceManagementApp.dto.response.CategoryResponse;
import com.example.FinanceManagementApp.exception.ApiException;
import com.example.FinanceManagementApp.model.entity.Category;
import com.example.FinanceManagementApp.model.entity.Users;
import com.example.FinanceManagementApp.model.enums.TransactionType;
import com.example.FinanceManagementApp.repository.CategoryRepo;
import com.example.FinanceManagementApp.security.CurrentUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepo categoryRepo;

    private String normalize(String s) {
        if (s == null) return null;
        return s.trim().toLowerCase(Locale.ROOT); //ı-i olayı
    }



    public List<CategoryResponse> getCategories(CurrentUserPrincipal principal, TransactionType type) {

        List<Category> categoryList=(type==null)
            ? categoryRepo.findByUserId(principal.getId())
            : categoryRepo.findByUserIdAndType(principal.getId(), type);

        List<CategoryResponse> responseList = new ArrayList<>();

        for (Category c : categoryList) {
            responseList.add(new CategoryResponse(c.getId(),c.getName(),c.getType()));
        }

        return responseList;


    }

    public Category create(@Valid CategoryRequest dto, CurrentUserPrincipal principal) {
        String name = normalize(dto.getName());
        Users user = principal.getUser();


        boolean exists =
                categoryRepo.existsByUserIdAndNameAndType(
                        user.getId(),
                        name,
                        dto.getType()
                );

        if (exists) {
            throw new ApiException(HttpStatus.CONFLICT, "Category already exists");
        }
        Category c = new Category();
        c.setName(name);
        c.setType(dto.getType());
        c.setUser(user);

        return categoryRepo.save(c);

    }


    public Category get(Long id, CurrentUserPrincipal principal) {
        Users user = principal.getUser();

        Category c = categoryRepo.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Category not found"));

        if (!c.getUser().getId().equals(user.getId()))
            throw new ApiException(HttpStatus.FORBIDDEN, "Access denied");

        return c;
    }


    public Category update(Long id, @Valid CategoryRequest dto, CurrentUserPrincipal principal) {
        Users user = principal.getUser();

        Category c = categoryRepo.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Category not found"));

        if (!c.getUser().getId().equals(user.getId()))
            throw new ApiException(HttpStatus.FORBIDDEN, "Access denied");

        c.setName(normalize(dto.getName()));
        c.setType(dto.getType());

        return categoryRepo.save(c);

    }
}
