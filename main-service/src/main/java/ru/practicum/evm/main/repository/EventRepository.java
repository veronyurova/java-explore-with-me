package ru.practicum.evm.main.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import ru.practicum.evm.main.model.Event;
import ru.practicum.evm.main.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    @Query("SELECT e FROM Event AS e " +
            "WHERE ((:users) IS NULL OR e.initiator.id IN :users) " +
            "AND ((:states) IS NULL OR e.state IN :states) " +
            "AND ((:categories) IS NULL OR e.category.id IN :categories) " +
            "AND (e.eventDate >= :rangeStart) " +
            "AND (CAST(:rangeEnd AS date) IS NULL OR e.eventDate <= :rangeEnd)")
    List<Event> searchEvents(List<Long> users,
                              List<EventState> states,
                              List<Long> categories,
                              LocalDateTime rangeStart,
                              LocalDateTime rangeEnd,
                              Pageable pageable);
}
