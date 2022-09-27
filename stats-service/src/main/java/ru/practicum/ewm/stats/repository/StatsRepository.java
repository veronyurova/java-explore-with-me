package ru.practicum.ewm.stats.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.stats.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {
    @Query("SELECT COUNT(eh.ip) FROM EndpointHit AS eh " +
            "WHERE eh.timestamp >= :start " +
            "AND eh.timestamp <= :end " +
            "AND ((:uris) IS NULL OR eh.uri IN :uris)")
    Long countHits(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT COUNT(DISTINCT eh.ip) FROM EndpointHit AS eh " +
            "WHERE eh.timestamp >= :start " +
            "AND eh.timestamp <= :end " +
            "AND ((:uris) IS NULL OR eh.uri IN :uris)")
    Long countUniqueHits(LocalDateTime start, LocalDateTime end, List<String> uris);

    Optional<EndpointHit> findFirstByUri(String uri);
}
