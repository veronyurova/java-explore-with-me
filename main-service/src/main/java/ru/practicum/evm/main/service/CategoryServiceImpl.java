package ru.practicum.evm.main.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import ru.practicum.evm.main.repository.CategoryRepository;
import ru.practicum.evm.main.mapper.CategoryMapper;
import ru.practicum.evm.main.model.Category;
import ru.practicum.evm.main.dto.CategoryDto;
import ru.practicum.evm.main.dto.NewCategoryDto;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.Min;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<CategoryDto> getCategories(@Min(0) int from, @Min(1) int size) {
        return categoryRepository.findAll(PageRequest.of(from / size, size))
                .stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        return CategoryMapper.toCategoryDto(findCategoryById(catId));
    }

    @Override
    public CategoryDto addCategory(@Valid NewCategoryDto newCategoryDto) {
        Category category = CategoryMapper.toCategoryAdd(newCategoryDto);
        Category addedCategory = categoryRepository.save(category);
        log.info("CategoryServiceImpl.addCategory: category {} successfully added",
                 addedCategory.getId());
        return CategoryMapper.toCategoryDto(addedCategory);
    }

    @Override
    public CategoryDto updateCategory(@Valid CategoryDto categoryDto) {
        Category newCategory = CategoryMapper.toCategoryUpdate(categoryDto);
        Category category = findCategoryById(newCategory.getId());
        category.setName(newCategory.getName());
        Category updatedCategory = categoryRepository.save(category);
        log.info("CategoryServiceImpl.updateCategory: category {} successfully updated",
                 category.getId());
        return CategoryMapper.toCategoryDto(updatedCategory);
    }

    @Override
    public void deleteCategoryById(Long catId) {
        categoryRepository.deleteById(catId);
        log.info("CategoryServiceImpl.deleteCategoryById: category {} successfully deleted",
                 catId);
    }

    private Category findCategoryById(Long catId) {
        Optional<Category> categoryOptional = categoryRepository.findById(catId);
        if (categoryOptional.isEmpty()) {
            String message = String.format("Category with id=%d was not found.", catId);
            log.warn("EntityNotFoundException at CategoryServiceImpl: {}", message);
            throw new EntityNotFoundException(message);
        }
        return categoryOptional.get();
    }
}
