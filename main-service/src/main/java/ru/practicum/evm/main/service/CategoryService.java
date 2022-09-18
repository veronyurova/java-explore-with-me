package ru.practicum.evm.main.service;

import ru.practicum.evm.main.dto.CategoryDto;
import ru.practicum.evm.main.dto.NewCategoryDto;

import javax.validation.constraints.Min;
import javax.validation.Valid;
import java.util.List;

public interface CategoryService {
    List<CategoryDto> getCategories(@Min(0) int from, @Min(1) int size);

    CategoryDto getCategoryById(Long catId);

    CategoryDto addCategory(@Valid NewCategoryDto newCategoryDto);

    CategoryDto updateCategory(@Valid CategoryDto categoryDto);

    void deleteCategoryById(Long catId);
}
