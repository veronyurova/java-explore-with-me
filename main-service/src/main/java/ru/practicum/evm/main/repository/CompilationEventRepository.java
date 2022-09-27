package ru.practicum.evm.main.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.evm.main.model.CompilationEvent;

import java.util.List;

@Repository
public interface CompilationEventRepository extends JpaRepository<CompilationEvent, Long> {
    @Query("SELECT ce.compilation FROM CompilationEvent AS ce " +
            "WHERE ce.compilation = :compId")
    List<Long> findCompilationEventIds(Long compId);

    @Transactional
    void deleteByCompilationAndEvent(Long compId, Long eventId);
}
