package ru.practicum.evm.main.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.evm.main.service.CategoryService;
import ru.practicum.evm.main.mapper.CategoryMapper;
import ru.practicum.evm.main.model.Category;
import ru.practicum.evm.main.dto.CategoryDto;
import ru.practicum.evm.main.dto.NewCategoryDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.stream.Collectors;
import java.util.List;

@RestController
@Validated
public class CategoryController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories")
    public List<CategoryDto> getCategories(@Min(0) @RequestParam(defaultValue = "0") int from,
                                           @Min(1) @RequestParam(defaultValue = "10") int size) {
        return categoryService.getCategories(from, size)
                .stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto getCategoryById(@PathVariable Long catId) {
        return CategoryMapper.toCategoryDto(categoryService.getCategoryById(catId));
    }

    @PostMapping("/admin/categories")
    public CategoryDto addCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        Category category = CategoryMapper.toCategoryAdd(newCategoryDto);
        return CategoryMapper.toCategoryDto(categoryService.addCategory(category));
    }

    @PatchMapping("/admin/categories")
    public CategoryDto updateCategory(@Valid @RequestBody CategoryDto categoryDto) {
        Category category = CategoryMapper.toCategoryUpdate(categoryDto);
        return CategoryMapper.toCategoryDto(categoryService.updateCategory(category));
    }

    @DeleteMapping("/admin/categories/{catId}")
    public void deleteCategoryById(@PathVariable Long catId) {
        categoryService.deleteCategoryById(catId);
    }
}
