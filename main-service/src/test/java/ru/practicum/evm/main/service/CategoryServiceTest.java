package ru.practicum.evm.main.service;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Page;
import ru.practicum.evm.main.repository.CategoryRepository;
import ru.practicum.evm.main.model.Category;
import ru.practicum.evm.main.dto.CategoryDto;
import ru.practicum.evm.main.dto.NewCategoryDto;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    private CategoryService categoryService;
    @Mock
    private CategoryRepository categoryRepository;
    private final Pageable pageable = Pageable.ofSize(10);
    private final Category category = new Category(1L, "Name");
    private final CategoryDto categoryDto = new CategoryDto(1L, "Name");

    @BeforeEach
    void beforeEach() {
        categoryService = new CategoryServiceImpl(categoryRepository);
    }

    @Test
    void getCategories() {
        Mockito.when(categoryRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(category)));

        List<CategoryDto> categoriesExpected = List.of(categoryDto);
        List<CategoryDto> categories = categoryService.getCategories(0, 10);

        assertNotNull(categories);
        assertEquals(categoriesExpected, categories);
    }

    @Test
    void getCategoriesNoCategories() {
        Mockito.when(categoryRepository.findAll(pageable)).thenReturn(Page.empty());

        List<CategoryDto> categories = categoryService.getCategories(0, 10);

        assertNotNull(categories);
        assertEquals(0, categories.size());
    }

    @Test
    void getCategoryById() {
        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        CategoryDto category = categoryService.getCategoryById(1L);

        assertNotNull(category);
        assertEquals(categoryDto, category);
    }

    @Test
    void getCategoryByIdNoSuchCategory() {
        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.getCategoryById(1L));
    }

    @Test
    void addCategory() {
        Category newCategory = new Category(null, "Name");
        NewCategoryDto newCategoryDto = new NewCategoryDto("Name");
        Mockito.when(categoryRepository.save(newCategory)).thenReturn(category);

        CategoryDto addedCategory = categoryService.addCategory(newCategoryDto);

        assertNotNull(addedCategory);
        assertEquals(categoryDto, addedCategory);
    }

    @Test
    void updateCategory() {
        CategoryDto categoryUpdateDto = new CategoryDto(1L, "UPD");
        Category categoryUpdated = new Category(1L, "UPD");
        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        Mockito.when(categoryRepository.save(categoryUpdated)).thenReturn(categoryUpdated);

        CategoryDto updatedCategory = categoryService.updateCategory(categoryUpdateDto);

        assertNotNull(updatedCategory);
        assertEquals(categoryUpdateDto, updatedCategory);
    }
}
