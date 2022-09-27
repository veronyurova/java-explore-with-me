package ru.practicum.evm.main.repository;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.evm.main.model.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CompilationEventRepositoryTest {
    @Autowired
    private TestEntityManager manager;
    @Autowired
    private CompilationEventRepository compilationEventRepository;
    private final User user = new User(null, "email@mail.com", "Name");
    private final Category category = new Category(null, "Name");
    private final Location location = new Location(5f, 5f);
    private final LocalDateTime date = LocalDateTime.of(2050, 1, 1, 0, 0);
    private final Event event1 = new Event(
            null,
            "Title 1",
            "Annotation 1",
            "Description 1",
            date,
            location,
            true,
            category,
            user,
            0,
            true,
            EventState.PUBLISHED,
            date,
            date
    );
    private final Event event2 = new Event(
            null,
            "Title 2",
            "Annotation 2",
            "Description 2",
            date,
            location,
            false,
            category,
            user,
            0,
            true,
            EventState.PENDING,
            date,
            date
    );
    private final Compilation compilation = new Compilation(null, "Title", true);
    private final CompilationEvent compilationEvent1 = new CompilationEvent(null, 1L, 1L);
    private final CompilationEvent compilationEvent2 = new CompilationEvent(null, 1L, 2L);

    @Test
    void findCompilationEventsIds() {
        manager.persist(user);
        manager.persist(category);
        manager.persist(event1);
        manager.persist(event2);
        manager.persist(compilation);
        manager.persist(compilationEvent1);
        manager.persist(compilationEvent2);

        List<Long> ids = compilationEventRepository.findCompilationEventIds(1L);

        assertNotNull(ids);
        assertEquals(2, ids.size());
    }

    @Test
    void findCompilationEventsIdsNoEvents() {
        manager.persist(compilation);

        List<Long> ids = compilationEventRepository.findCompilationEventIds(1L);

        assertNotNull(ids);
        assertEquals(0, ids.size());
    }
}
