package ru.practicum.ewm.stats.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.ewm.stats.repository.StatsRepository;
import ru.practicum.ewm.stats.model.EndpointHit;
import ru.practicum.ewm.stats.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Autowired
    public StatsServiceImpl(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    @Override
    public void saveHit(EndpointHit endpointHit) {
        EndpointHit addedHit = statsRepository.save(endpointHit);
        log.info("StatsServiceImpl.saveHit: endpoint hit {} successfully added", addedHit.getId());
    }

    @Override
    public ViewStats getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        Long hits;
        if (unique) {
            hits = statsRepository.countUniqueHits(start, end, uris);
        } else {
            hits = statsRepository.countHits(start, end, uris);
        }
        String app = "ALL";
        String uri = "ALL";
        if (uris != null && uris.size() > 0) {
            uri = uris.get(0);
            Optional<EndpointHit> hitOptional = statsRepository.findFirstByUri(uris.get(0));
            if (hitOptional.isPresent()) app = hitOptional.get().getApp();
        }
        return new ViewStats(app, uri, hits);
    }
}
