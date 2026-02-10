package com.example.FinanceManagementApp.controller;

import com.example.FinanceManagementApp.dto.request.CategoryRequest;
import com.example.FinanceManagementApp.dto.response.CategoryResponse;
import com.example.FinanceManagementApp.model.enums.TransactionType;
import com.example.FinanceManagementApp.security.CurrentUserPrincipal;
import com.example.FinanceManagementApp.service.CategoryServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/categories")
public class CategoryController {

    private final CategoryServiceImpl categoryService;


    //
    //   /api/categories?type=EXPENSE
    @GetMapping()
    public ResponseEntity<List<CategoryResponse>> getCategories(@RequestParam(required = false) TransactionType type, @AuthenticationPrincipal CurrentUserPrincipal principal) {

        return  ResponseEntity.ok(categoryService.getCategories(principal,type));

    }


    @PostMapping
    public ResponseEntity<CategoryResponse> create(@AuthenticationPrincipal CurrentUserPrincipal principal,
            @RequestBody @Valid CategoryRequest dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new CategoryResponse(categoryService.create(dto, principal)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> get(
            @PathVariable Long id,
            @AuthenticationPrincipal CurrentUserPrincipal principal) {

        return ResponseEntity.ok(new CategoryResponse(categoryService.get(id, principal)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid CategoryRequest dto,
            @AuthenticationPrincipal CurrentUserPrincipal principal) {

        return ResponseEntity.ok(new CategoryResponse(categoryService.update(id, dto, principal)));
    }
}
