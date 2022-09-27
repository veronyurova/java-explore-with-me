package ru.practicum.evm.main.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.data.domain.Pageable;
import ru.practicum.evm.main.model.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class EventRepositoryTest {
    @Autowired
    private TestEntityManager manager;
    @Autowired
    private EventRepository eventRepository;
    private final Pageable pageable = Pageable.ofSize(10);
    private final User user1 = new User(null, "email1@mail.com", "Name 1");
    private final User user2 = new User(null, "email2@mail.com", "Name 2");
    private final Category category1 = new Category(null, "Name 1");
    private final Category category2 = new Category(null, "Name 2");
    private final Location location = new Location(5f, 5f);
    private final LocalDateTime date1 = LocalDateTime.of(2050, 1, 1, 0, 0);
    private final LocalDateTime date2 = LocalDateTime.of(2100, 1, 1, 0, 0);
    private final LocalDateTime rangeStart = LocalDateTime.of(2049, 1, 1, 0, 0);
    private final LocalDateTime rangeEnd = LocalDateTime.of(2051, 1, 1, 0, 0);
    private final Event event1 = new Event(
            null,
            "Title 1",
            "Annotation 1",
            "Description 1",
            date1,
            location,
            true,
            category1,
            user1,
            0,
            true,
            EventState.PUBLISHED,
            date1,
            date1
    );
    private final Event event2 = new Event(
            null,
            "Title 2",
            "Annotation 2",
            "Description 2",
            date2,
            location,
            false,
            category2,
            user2,
            0,
            true,
            EventState.PENDING,
            date2,
            date2
    );
    private final Event eventExpected = new Event(
            1L,
            "Title 1",
            "Annotation 1",
            "Description 1",
            date1,
            location,
            true,
            category1,
            user1,
            0,
            true,
            EventState.PUBLISHED,
            date1,
            date1
    );

    @BeforeEach
    void beforeEach() {
        manager.persist(user1);
        manager.persist(user2);
        manager.persist(category1);
        manager.persist(category2);
        manager.persist(event1);
        manager.persist(event2);
    }

    @Test
    void findEventsAllParams() {
        List<Event> eventsExpected = List.of(eventExpected);

        List<Event> events = eventRepository.findEvents("annotation 1", List.of(1L), true,
                rangeStart, rangeEnd);

        assertNotNull(events);
        assertEquals(eventsExpected, events);
    }

    @Test
    void findEventsNullParams() {
        List<Event> events = eventRepository.findEvents(null, null, null, rangeStart, null);

        assertNotNull(events);
        assertEquals(2, events.size());
    }

    @Test
    void findEventsByAnnotation() {
        List<Event> eventsExpected = List.of(eventExpected);

        List<Event> events = eventRepository.findEvents("annotation 1", null, null, rangeStart, null);

        assertNotNull(events);
        assertEquals(eventsExpected, events);
    }

    @Test
    void findEventsByDescription() {
        List<Event> eventsExpected = List.of(eventExpected);

        List<Event> events = eventRepository.findEvents("description 1", null, null, rangeStart, null);

        assertNotNull(events);
        assertEquals(eventsExpected.get(0), events.get(0));
    }

    @Test
    void findEventsByCategory() {
        List<Event> eventsExpected = List.of(eventExpected);

        List<Event> events = eventRepository.findEvents(null, List.of(1L), null, rangeStart, null);

        assertNotNull(events);
        assertEquals(eventsExpected, events);
    }

    @Test
    void findEventsByPaid() {
        List<Event> eventsExpected = List.of(eventExpected);

        List<Event> events = eventRepository.findEvents(null, null, true, rangeStart, null);

        assertNotNull(events);
        assertEquals(eventsExpected, events);
    }

    @Test
    void findEventsByDateRange() {
        List<Event> eventsExpected = List.of(eventExpected);

        List<Event> events = eventRepository.findEvents(null, null, null, rangeStart, rangeEnd);

        assertNotNull(events);
        assertEquals(eventsExpected, events);
    }

    @Test
    void adminSearchEventsAllParams() {
        List<Event> eventsExpected = List.of(eventExpected);

        List<Event> events = eventRepository.searchEvents(List.of(1L), List.of(EventState.PUBLISHED),
                List.of(1L), rangeStart, rangeEnd, pageable);

        assertNotNull(events);
        assertEquals(eventsExpected, events);
    }

    @Test
    void adminSearchEventsNullParams() {
        List<Event> events = eventRepository.searchEvents(null, null, null,
                rangeStart, null, pageable);

        assertNotNull(events);
        assertEquals(2, events.size());
    }

    @Test
    void adminSearchEventsByUsers() {
        List<Event> eventsExpected = List.of(eventExpected);

        List<Event> events = eventRepository.searchEvents(List.of(1L), null, null,
                rangeStart, null, pageable);

        assertNotNull(events);
        assertEquals(eventsExpected, events);
    }

    @Test
    void adminSearchEventsByStates() {
        List<Event> eventsExpected = List.of(eventExpected);

        List<Event> events = eventRepository.searchEvents(null, List.of(EventState.PUBLISHED), null,
                rangeStart, null, pageable);

        assertNotNull(events);
        assertEquals(eventsExpected, events);
    }

    @Test
    void adminSearchEventsByCategories() {
        List<Event> eventsExpected = List.of(eventExpected);

        List<Event> events = eventRepository.searchEvents(null, null, List.of(1L),
                rangeStart, null, pageable);

        assertNotNull(events);
        assertEquals(eventsExpected, events);
    }

    @Test
    void adminSearchEventsByDateRange() {
        List<Event> eventsExpected = List.of(eventExpected);

        List<Event> events = eventRepository.searchEvents(null, null, null,
                rangeStart, rangeEnd, pageable);

        assertNotNull(events);
        assertEquals(eventsExpected, events);
    }
}
