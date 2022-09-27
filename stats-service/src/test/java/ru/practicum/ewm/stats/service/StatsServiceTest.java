package ru.practicum.ewm.stats.service;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.ewm.stats.model.ViewStats;
import ru.practicum.ewm.stats.repository.StatsRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class StatsServiceTest {
    private StatsService statsService;
    @Mock
    private StatsRepository statsRepository;
    private final ViewStats statsExpected = new ViewStats("ALL", "ALL", 1L);
    private final LocalDateTime date = LocalDateTime.of(2050, 1, 1, 0, 0);

    @BeforeEach
    void beforeEach() {
        statsService = new StatsServiceImpl(statsRepository);
    }

    @Test
    void getStats() {
        Mockito
                .when(statsRepository.countHits(date, date, Mockito.any()))
                .thenReturn(1L);

        ViewStats stats = statsService.getStats(date, date, null, false);

        assertNotNull(stats);
        assertEquals(statsExpected, stats);
    }

    @Test
    void getStatsUnique() {
        Mockito
                .when(statsRepository.countUniqueHits(date, date, Mockito.any()))
                .thenReturn(1L);

        ViewStats stats = statsService.getStats(date, date, null, true);

        assertNotNull(stats);
        assertEquals(statsExpected, stats);
    }
}
