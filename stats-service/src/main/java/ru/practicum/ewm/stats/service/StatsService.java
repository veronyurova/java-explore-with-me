package ru.practicum.ewm.stats.service;

import ru.practicum.ewm.stats.model.ViewStats;
import ru.practicum.ewm.stats.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    void saveHit(EndpointHit endpointHit);

    ViewStats getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
