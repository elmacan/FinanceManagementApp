package com.example.FinanceManagementApp.dto.response;

import com.example.FinanceManagementApp.model.entity.Category;
import com.example.FinanceManagementApp.model.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CategoryResponse {

    private Long id;
    private String name;
    private TransactionType type;

    public CategoryResponse(Category c) {
        this.id = c.getId();
        this.name = c.getName();
        this.type = c.getType();
    }

}
