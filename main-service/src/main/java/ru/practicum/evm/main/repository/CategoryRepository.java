package ru.practicum.evm.main.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.evm.main.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
