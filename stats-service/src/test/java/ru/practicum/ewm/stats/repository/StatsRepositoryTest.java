package ru.practicum.ewm.stats.repository;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.ewm.stats.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class StatsRepositoryTest {
    @Autowired
    private TestEntityManager manager;
    @Autowired
    private StatsRepository statsRepository;
    private final EndpointHit hit1 = new EndpointHit(null, "APP", "URI", "IP", LocalDateTime.now());
    private final EndpointHit hit2 = new EndpointHit(null, "APP", "URI", "IP", LocalDateTime.now());

    @Test
    void countHits() {
        manager.persist(hit1);
        manager.persist(hit2);

        Long count = statsRepository.countHits(LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1), List.of("URI"));

        assertNotNull(count);
        assertEquals(2L, count);
    }

    @Test
    void countHitsNoHits() {
        Long count = statsRepository.countHits(LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1), List.of("URI"));

        assertNotNull(count);
        assertEquals(0L, count);
    }

    @Test
    void countUniqueHits() {
        manager.persist(hit1);
        manager.persist(hit2);

        Long count = statsRepository.countUniqueHits(LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1), List.of("URI"));

        assertNotNull(count);
        assertEquals(1L, count);
    }

    @Test
    void countUniqueHitsNoHits() {
        Long count = statsRepository.countUniqueHits(LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1), List.of("URI"));

        assertNotNull(count);
        assertEquals(0L, count);
    }
}
