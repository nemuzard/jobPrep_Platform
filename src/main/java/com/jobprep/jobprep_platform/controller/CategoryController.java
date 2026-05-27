package com.jobprep.jobprep_platform.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobprep.jobprep_platform.model.base.ApiResponse;
import com.jobprep.jobprep_platform.model.base.EmptyVO;
import com.jobprep.jobprep_platform.model.dto.category.CreateCategoryBody;
import com.jobprep.jobprep_platform.model.dto.category.UpdateCategoryBody;
import com.jobprep.jobprep_platform.model.vo.category.CategoryVO;
import com.jobprep.jobprep_platform.model.vo.category.CreateCategoryVO;
import com.jobprep.jobprep_platform.service.CategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ApiResponse<List<CategoryVO>> getCategories() {
        return categoryService.categoryList();
    }

    @PostMapping
    public ApiResponse<CreateCategoryVO> createCategory(@Valid @RequestBody CreateCategoryBody body) {
        return categoryService.createCategory(body);
    }

    @PatchMapping("/{categoryId}")
    public ApiResponse<EmptyVO> updateCategory(
            @PathVariable Integer categoryId,
            @Valid @RequestBody UpdateCategoryBody body) {
        return categoryService.updateCategory(categoryId, body);
    }

    @DeleteMapping("/{categoryId}")
    public ApiResponse<EmptyVO> deleteCategory(@PathVariable Integer categoryId) {
        return categoryService.deleteCategory(categoryId);
    }
}
