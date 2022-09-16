package ru.practicum.evm.main.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.evm.main.model.Category;
import ru.practicum.evm.main.repository.CategoryRepository;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Page<Category> getCategories(int from, int size) {
        return categoryRepository.findAll(PageRequest.of(from / size, size));
    }

    @Override
    public Category getCategoryById(Long catId) {
        Optional<Category> categoryOptional = categoryRepository.findById(catId);
        if (categoryOptional.isEmpty()) {
            String message = String.format("Category with id=%d was not found.", catId);
            log.warn("EntityNotFoundException at CategoryServiceImpl.updateCategory: {}", message);
            throw new EntityNotFoundException(message);
        }
        return categoryOptional.get();
    }

    @Override
    public Category addCategory(Category category) {
        Category addedCategory = categoryRepository.save(category);
        log.info("CategoryServiceImpl.addCategory: category {} successfully added",
                 addedCategory.getId());
        return addedCategory;
    }

    @Override
    public Category updateCategory(Category newCategory) {
        Category category = getCategoryById(newCategory.getId());
        category.setName(newCategory.getName());
        Category updatedCategory = categoryRepository.save(category);
        log.info("CategoryServiceImpl.updateCategory: category {} successfully updated",
                 category.getId());
        return updatedCategory;
    }

    @Override
    public void deleteCategoryById(Long catId) {
        categoryRepository.deleteById(catId);
        log.info("CategoryServiceImpl.deleteCategoryById: category {} successfully deleted",
                 catId);
    }
}
