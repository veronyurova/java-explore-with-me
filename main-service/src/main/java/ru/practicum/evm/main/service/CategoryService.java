package ru.practicum.evm.main.service;

import org.springframework.data.domain.Page;
import ru.practicum.evm.main.model.Category;

public interface CategoryService {
    Page<Category> getCategories(int from, int size);

    Category getCategoryById(Long catId);

    Category addCategory(Category category);

    Category updateCategory(Category newCategory);

    void deleteCategoryById(Long catId);
}
