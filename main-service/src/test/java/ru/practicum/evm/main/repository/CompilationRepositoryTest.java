package ru.practicum.evm.main.repository;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.data.domain.Pageable;
import ru.practicum.evm.main.model.Compilation;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CompilationRepositoryTest {
    @Autowired
    private TestEntityManager manager;
    @Autowired
    private CompilationRepository compilationRepository;
    private final Pageable pageable = Pageable.ofSize(10);
    private final Compilation compilation1 = new Compilation(null, "Compilation", true);
    private final Compilation compilation2 = new Compilation(null, "Compilation", false);

    @Test
    void findPinnedCompilations() {
        manager.persist(compilation1);
        manager.persist(compilation2);
        Compilation compilationExpected = new Compilation(1L, "Compilation", true);
        List<Compilation> compilationsExpected = List.of(compilationExpected);

        List<Compilation> compilations = compilationRepository.findAllByPinned(true, pageable);

        assertNotNull(compilations);
        assertEquals(compilationsExpected, compilations);
    }

    @Test
    void findPinnedNoCompilations() {
        List<Compilation> compilations = compilationRepository.findAllByPinned(true, pageable);

        assertNotNull(compilations);
        assertEquals(0, compilations.size());
    }

    @Test
    void findCompilationsByNullPinned() {
        manager.persist(compilation1);
        manager.persist(compilation2);

        List<Compilation> compilations = compilationRepository.findAllByPinned(null, pageable);

        assertNotNull(compilations);
        assertEquals(2, compilations.size());
    }
}
