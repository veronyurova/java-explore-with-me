package ru.practicum.evm.main.mapper;

import ru.practicum.evm.main.dto.CategoryDto;
import ru.practicum.evm.main.dto.NewCategoryDto;
import ru.practicum.evm.main.model.Category;

public class CategoryMapper {
    public static Category toCategoryAdd(NewCategoryDto newCategoryDto) {
        return new Category(null, newCategoryDto.getName());
    }

    public static Category toCategoryUpdate(CategoryDto categoryDto) {
        return new Category(categoryDto.getId(), categoryDto.getName());
    }

    public static CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }
}
