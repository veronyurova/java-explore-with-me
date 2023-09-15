package ru.practicum.evm.main.service;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.evm.main.repository.EventRepository;
import ru.practicum.evm.main.mapper.EventMapper;
import ru.practicum.evm.main.dto.*;
import ru.practicum.evm.main.model.*;
import ru.practicum.evm.main.exception.ForbiddenOperationException;
import ru.practicum.evm.main.exception.IncorrectEventStateException;
import ru.practicum.evm.main.exception.IncorrectSortCriteriaException;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    private EventService eventService;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventMapper eventMapper;
    private final Pageable pageable = Pageable.ofSize(10);
    private final Location location = new Location(5f, 5f);
    private final Category category = new Category(1L, "Name");
    private final CategoryDto categoryDto = new CategoryDto(1L, "Name");
    private final User user = new User(1L, "email@mail.com", "Name");
    private final UserShortDto userDto = new UserShortDto(1L, "Name");
    private final LocalDateTime date = LocalDateTime.of(2050, 1, 1, 0, 0);
    private final LocalDateTime rangeStart = LocalDateTime.of(2049, 1, 1, 0, 0);
    private final LocalDateTime rangeEnd = LocalDateTime.of(2051, 1, 1, 0, 0);
    private final Event event = new Event(
            1L,
            "Title",
            "Annotation",
            "Description",
            date,
            location,
            true,
            category,
            user,
            0,
            false,
            EventState.PUBLISHED,
            date,
            date
    );
    private final EventShortDto eventShortDto = new EventShortDto(
            1L,
            "Title",
            "Annotation",
            date,
            true,
            categoryDto,
            userDto,
            0,
            0L,
            1.0F
    );
    private final EventFullDto eventFullDto = new EventFullDto(
            1L,
            "Title",
            "Annotation",
            "Description",
            date,
            location,
            true,
            categoryDto,
            userDto,
            0,
            0,
            false,
            "PUBLISHED",
            date,
            date,
            0L,
            1.0F
    );

    @BeforeEach
    void beforeEach() {
        eventService = new EventServiceImpl(eventRepository, eventMapper);
    }

    @Test
    void getPublishedEventById() {
        Mockito.when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        Mockito.when(eventMapper.toEventFullDto(event)).thenReturn(eventFullDto);

        EventFullDto publishedEvent = eventService.getPublishedEventById(1L);

        assertNotNull(publishedEvent);
        assertEquals(eventFullDto, publishedEvent);
    }

    @Test
    void getPublishedEventByIdNoSuchEvent() {
        Mockito.when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> eventService.getPublishedEventById(1L));
    }

    @Test
    void getPublishedEventByIdIncorrectState() {
        Event eventPending = new Event(
                1L,
                "Title",
                "Annotation",
                "Description",
                date,
                location,
                true,
                category,
                user,
                0,
                false,
                EventState.PENDING,
                date,
                date
        );
        Mockito.when(eventRepository.findById(1L)).thenReturn(Optional.of(eventPending));

        assertThrows(ForbiddenOperationException.class, () -> eventService.getPublishedEventById(1L));
    }

    @Test
    void getUserEventById() {
        Mockito.when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        Mockito.when(eventMapper.toEventFullDto(event)).thenReturn(eventFullDto);

        EventFullDto userEvent = eventService.getUserEventById(1L, 1L);

        assertNotNull(userEvent);
        assertEquals(eventFullDto, userEvent);
    }

    @Test
    void getUserEventByIdNoSuchEvent() {
        Mockito.when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> eventService.getUserEventById(1L, 1L));
    }

    @Test
    void getUserEventByIdIncorrectInitiator() {
        Mockito.when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThrows(ForbiddenOperationException.class, () -> eventService.getUserEventById(2L, 1L));
    }

    @Test
    void getUserEvents() {
        Mockito.when(eventRepository.findAllByInitiatorId(1L, pageable)).thenReturn(List.of(event));
        Mockito.when(eventMapper.toEventFullDto(event)).thenReturn(eventFullDto);

        List<EventFullDto> eventsExpected = List.of(eventFullDto);
        List<EventFullDto> events = eventService.getUserEvents(1L, 0, 10);

        assertNotNull(events);
        assertEquals(eventsExpected, events);
    }

    @Test
    void getUserEventsNoEvents() {
        Mockito
                .when(eventRepository.findAllByInitiatorId(1L, pageable))
                .thenReturn(Collections.emptyList());

        List<EventFullDto> events = eventService.getUserEvents(1L, 0, 10);

        assertNotNull(events);
        assertEquals(0, events.size());
    }

    @Test
    void getEvents() {
        Mockito
                .when(eventRepository.findEvents("annotation", List.of(1L), true, rangeStart, rangeEnd))
                .thenReturn(List.of(event));
        Mockito.when(eventMapper.toEventFullDto(event)).thenReturn(eventFullDto);
        Mockito.when(eventMapper.toEventShortFromFull(eventFullDto)).thenReturn(eventShortDto);

        List<EventShortDto> eventsExpected = List.of(eventShortDto);
        List<EventShortDto> events = eventService.getEvents("annotation", List.of(1L), true,
                rangeStart, rangeEnd, true, "EVENT_DATE", 0, 10);

        assertNotNull(events);
        assertEquals(eventsExpected, events);
    }

    @Test
    void getEventsByViews() {
        Mockito
                .when(eventRepository.findEvents("annotation", List.of(1L), true, rangeStart, rangeEnd))
                .thenReturn(List.of(event));
        Mockito.when(eventMapper.toEventFullDto(event)).thenReturn(eventFullDto);
        Mockito.when(eventMapper.toEventShortFromFull(eventFullDto)).thenReturn(eventShortDto);

        List<EventShortDto> eventsExpected = List.of(eventShortDto);
        List<EventShortDto> events = eventService.getEvents("annotation", List.of(1L), true,
                rangeStart, rangeEnd, true, "VIEWS", 0, 10);

        assertNotNull(events);
        assertEquals(eventsExpected, events);
    }

    @Test
    void getEventsIncorrectSort() {
        Mockito
                .when(eventRepository.findEvents("annotation", List.of(1L), true, rangeStart, rangeEnd))
                .thenReturn(List.of(event));
        Mockito.when(eventMapper.toEventFullDto(event)).thenReturn(eventFullDto);

        assertThrows(IncorrectSortCriteriaException.class, () -> eventService.getEvents("annotation",
                List.of(1L), true, rangeStart, rangeEnd, true, "INCORRECT", 0, 10));
    }

    @Test
    void adminSearchEvents() {
        Mockito
                .when(eventRepository.searchEvents(List.of(1L), List.of(EventState.PUBLISHED),
                        List.of(1L), rangeStart, rangeEnd, pageable))
                .thenReturn(List.of(event));
        Mockito.when(eventMapper.toEventFullDto(event)).thenReturn(eventFullDto);

        List<EventFullDto> eventsExpected = List.of(eventFullDto);
        List<EventFullDto> events = eventService.searchEvents(List.of(1L), List.of("PUBLISHED"),
                List.of(1L), rangeStart, rangeEnd, 0, 10);

        assertNotNull(events);
        assertEquals(eventsExpected, events);
    }

    @Test
    void adminSearchIncorrectState() {
        assertThrows(IncorrectEventStateException.class, () ->  eventService.searchEvents(List.of(1L),
                List.of("INCORRECT"), List.of(1L), rangeStart, rangeEnd, 0, 10));
    }

    @Test
    void updateEvent() {
        LocalDateTime newDate = LocalDateTime.of(2100, 1, 1, 0, 0);
        Event oldEvent = new Event(
                1L,
                "Title",
                "Annotation",
                "Description",
                date,
                location,
                true,
                category,
                user,
                0,
                false,
                EventState.PENDING,
                date,
                date
        );
        UpdateEventRequest updateEventRequest = new UpdateEventRequest(
                1L,
                "UPD",
                "UPD",
                "UPD",
                newDate,
                false,
                null,
                100
        );
        Event updateEvent = new Event(
                1L,
                "UPD",
                "UPD",
                "UPD",
                newDate,
                location,
                false,
                category,
                user,
                100,
                false,
                EventState.PENDING,
                date,
                date
        );
        EventFullDto updatedEventFullDto = new EventFullDto(
                1L,
                "UPD",
                "UPD",
                "UPD",
                newDate,
                location,
                false,
                categoryDto,
                userDto,
                100,
                0,
                false,
                "PENDING",
                date,
                date,
                0L,
                1.0F
        );
        Mockito.when(eventRepository.findById(1L)).thenReturn(Optional.of(oldEvent));
        Mockito.when(eventRepository.save(updateEvent)).thenReturn(updateEvent);
        Mockito.when(eventMapper.toEventFullDto(updateEvent)).thenReturn(updatedEventFullDto);

        EventFullDto updatedEvent = eventService.updateEvent(1L, updateEventRequest);

        assertNotNull(updatedEvent);
        assertEquals(updatedEventFullDto, updatedEvent);
    }

    @Test
    void updateEventIncorrectInitiator() {
        UpdateEventRequest updateEventRequest = new UpdateEventRequest(
                1L,
                "UPD",
                "UPD",
                "UPD",
                LocalDateTime.of(2100, 1, 1, 0, 0),
                false,
                null,
                100
        );
        Mockito.when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThrows(ForbiddenOperationException.class, () -> eventService.updateEvent(2L,
                updateEventRequest));
    }

    @Test
    void updateEventIncorrectEventState() {
        UpdateEventRequest updateEventRequest = new UpdateEventRequest(
                1L,
                "UPD",
                "UPD",
                "UPD",
                LocalDateTime.of(2100, 1, 1, 0, 0),
                false,
                null,
                100
        );
        Mockito.when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThrows(ForbiddenOperationException.class, () -> eventService.updateEvent(1L,
                updateEventRequest));
    }

    @Test
    void adminUpdateEvent() {
        LocalDateTime newDate = LocalDateTime.of(2100, 1, 1, 0, 0);
        AdminUpdateEventRequest updateEventRequest = new AdminUpdateEventRequest(
                "UPD",
                "UPD",
                "UPD",
                newDate,
                null,
                false,
                null,
                100,
                true
        );
        Event updateEvent = new Event(
                1L,
                "UPD",
                "UPD",
                "UPD",
                newDate,
                location,
                false,
                category,
                user,
                100,
                true,
                EventState.PUBLISHED,
                date,
                date
        );
        EventFullDto updatedEventFullDto = new EventFullDto(
                1L,
                "UPD",
                "UPD",
                "UPD",
                newDate,
                location,
                false,
                categoryDto,
                userDto,
                100,
                0,
                true,
                "PUBLISHED",
                date,
                date,
                0L,
                1.0F
        );
        Mockito.when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        Mockito.when(eventRepository.save(updateEvent)).thenReturn(updateEvent);
        Mockito.when(eventMapper.toEventFullDto(updateEvent)).thenReturn(updatedEventFullDto);

        EventFullDto updatedEvent = eventService.adminUpdateEvent(1L, updateEventRequest);

        assertNotNull(updatedEvent);
        assertEquals(updatedEventFullDto, updatedEvent);
    }

    @Test
    void cancelEvent() {
        Event oldEvent = new Event(
                1L,
                "Title",
                "Annotation",
                "Description",
                date,
                location,
                true,
                category,
                user,
                0,
                false,
                EventState.PENDING,
                date,
                date
        );
        Event cancelEvent = new Event(
                1L,
                "Title",
                "Annotation",
                "Description",
                date,
                location,
                true,
                category,
                user,
                0,
                false,
                EventState.CANCELED,
                date,
                date
        );
        EventFullDto updatedEventFullDto = new EventFullDto(
                1L,
                "Title",
                "Annotation",
                "Description",
                date,
                location,
                true,
                categoryDto,
                userDto,
                0,
                0,
                false,
                "CANCELED",
                date,
                date,
                0L,
                1.0F
        );
        Mockito.when(eventRepository.findById(1L)).thenReturn(Optional.of(oldEvent));
        Mockito.when(eventRepository.save(cancelEvent)).thenReturn(cancelEvent);
        Mockito.when(eventMapper.toEventFullDto(cancelEvent)).thenReturn(updatedEventFullDto);

        EventFullDto cancelledEvent = eventService.cancelEvent(1L, 1L);

        assertNotNull(cancelledEvent);
        assertEquals(updatedEventFullDto, cancelledEvent);
    }

    @Test
    void cancelEventIncorrectInitiator() {
        Mockito.when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThrows(ForbiddenOperationException.class, () -> eventService.cancelEvent(2L, 1L));
    }

    @Test
    void cancelEventIncorrectEventState() {
        Mockito.when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThrows(ForbiddenOperationException.class, () -> eventService.cancelEvent(1L, 1L));
    }

    @Test
    void publishEvent() {
        Event oldEvent = new Event(
                1L,
                "Title",
                "Annotation",
                "Description",
                date,
                location,
                true,
                category,
                user,
                0,
                false,
                EventState.PENDING,
                date,
                date
        );
        Event publishEvent = new Event(
                1L,
                "Title",
                "Annotation",
                "Description",
                date,
                location,
                true,
                category,
                user,
                0,
                false,
                EventState.PUBLISHED,
                date,
                date
        );
        EventFullDto updatedEventFullDto = new EventFullDto(
                1L,
                "Title",
                "Annotation",
                "Description",
                date,
                location,
                true,
                categoryDto,
                userDto,
                0,
                0,
                false,
                "PUBLISHED",
                date,
                date,
                0L,
                1.0F
        );
        Mockito.when(eventRepository.findById(1L)).thenReturn(Optional.of(oldEvent));
        Mockito.when(eventRepository.save(publishEvent)).thenReturn(publishEvent);
        Mockito.when(eventMapper.toEventFullDto(publishEvent)).thenReturn(updatedEventFullDto);

        EventFullDto publishedEvent = eventService.publishEvent(1L);

        assertNotNull(publishedEvent);
        assertEquals(updatedEventFullDto, publishedEvent);
    }

    @Test
    void publishEventIncorrectEventDate() {
        Event oldEvent = new Event(
                1L,
                "Title",
                "Annotation",
                "Description",
                LocalDateTime.now(),
                location,
                true,
                category,
                user,
                0,
                false,
                EventState.PENDING,
                date,
                date
        );
        Mockito.when(eventRepository.findById(1L)).thenReturn(Optional.of(oldEvent));

        assertThrows(ForbiddenOperationException.class, () -> eventService.publishEvent(1L));
    }

    @Test
    void publishEventIncorrectEventState() {
        Mockito.when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThrows(ForbiddenOperationException.class, () -> eventService.publishEvent(1L));
    }

    @Test
    void rejectEvent() {
        Event oldEvent = new Event(
                1L,
                "Title",
                "Annotation",
                "Description",
                date,
                location,
                true,
                category,
                user,
                0,
                false,
                EventState.PENDING,
                date,
                date
        );
        Event rejectEvent = new Event(
                1L,
                "Title",
                "Annotation",
                "Description",
                date,
                location,
                true,
                category,
                user,
                0,
                false,
                EventState.CANCELED,
                date,
                date
        );
        EventFullDto updatedEventFullDto = new EventFullDto(
                1L,
                "Title",
                "Annotation",
                "Description",
                date,
                location,
                true,
                categoryDto,
                userDto,
                0,
                0,
                false,
                "CANCELED",
                date,
                date,
                0L,
                1.0F
        );
        Mockito.when(eventRepository.findById(1L)).thenReturn(Optional.of(oldEvent));
        Mockito.when(eventRepository.save(rejectEvent)).thenReturn(rejectEvent);
        Mockito.when(eventMapper.toEventFullDto(rejectEvent)).thenReturn(updatedEventFullDto);

        EventFullDto rejectedEvent = eventService.rejectEvent(1L);

        assertNotNull(rejectedEvent);
        assertEquals(updatedEventFullDto, rejectedEvent);
    }

    @Test
    void rejectEventIncorrectEventState() {
        Mockito.when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThrows(ForbiddenOperationException.class, () -> eventService.rejectEvent(1L));
    }
}
