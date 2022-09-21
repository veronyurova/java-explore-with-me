package ru.practicum.evm.main.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import ru.practicum.evm.main.repository.EventRepository;
import ru.practicum.evm.main.mapper.EventMapper;
import ru.practicum.evm.main.model.*;
import ru.practicum.evm.main.dto.EventFullDto;
import ru.practicum.evm.main.dto.NewEventDto;
import ru.practicum.evm.main.dto.UpdateEventRequest;
import ru.practicum.evm.main.dto.AdminUpdateEventRequest;
import ru.practicum.evm.main.exception.ForbiddenOperationException;
import ru.practicum.evm.main.exception.IncorrectEventStateException;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.Min;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public List<EventFullDto> getUserEvents(Long userId, @Min(0) int from, @Min(1) int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return eventRepository.findAllByInitiatorId(userId, pageable)
                .stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto addEvent(Long userId, @Valid NewEventDto newEventDto) {
        Event event = EventMapper.toEventAdd(newEventDto);
        event.setInitiator(new User(userId, null, null));
        Event addedEvent = eventRepository.save(event);
        log.info("EventServiceImpl.addEvent: event {} successfully added", addedEvent.getId());
        return EventMapper.toEventFullDto(addedEvent);
    }

    @Override
    public EventFullDto updateEvent(Long userId, @Valid UpdateEventRequest newEvent) {
        Event event = findEventById(newEvent.getEventId());
        if (!userId.equals(event.getInitiator().getId())) {
            String message = "Only initiator can update event.";
            log.warn("ForbiddenOperationException at EventServiceImpl.updateEvent: {}", message);
            throw new ForbiddenOperationException(message);
        }
        EventState state = event.getState();
        if (!EventState.CANCELED.equals(state) && !EventState.PENDING.equals(state)) {
            String message = "Only pending or canceled events can be changed.";
            log.warn("ForbiddenOperationException at EventServiceImpl.updateEvent: {}", message);
            throw new ForbiddenOperationException(message);
        }
        if (newEvent.getTitle() != null) event.setTitle(newEvent.getTitle());
        if (newEvent.getAnnotation() != null) event.setAnnotation(newEvent.getAnnotation());
        if (newEvent.getDescription() != null) event.setDescription(newEvent.getDescription());
        if (newEvent.getEventDate() != null) event.setEventDate(newEvent.getEventDate());
        if (newEvent.getPaid() != null) event.setPaid(newEvent.getPaid());
        if (newEvent.getCategory() != null) {
            event.setCategory(new Category(newEvent.getCategory(), null));
        }
        if (newEvent.getParticipantLimit() != null) {
            event.setParticipantLimit(newEvent.getParticipantLimit());
        }
        event.setState(EventState.PENDING);
        Event updatedEvent = eventRepository.save(event);
        log.info("EventServiceImpl.updateEvent: event {} successfully updated", event.getId());
        return EventMapper.toEventFullDto(updatedEvent);
    }

    @Override
    public EventFullDto getUserEventById(Long userId, Long eventId) {
        Event event = findEventById(eventId);
        if (!userId.equals(event.getInitiator().getId())) {
            String message = "Only initiator can get full event.";
            log.warn("ForbiddenOperationException at EventServiceImpl: {}", message);
            throw new ForbiddenOperationException(message);
        }
        return EventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto cancelEvent(Long userId, Long eventId) {
        Event event = findEventById(eventId);
        if (!userId.equals(event.getInitiator().getId())) {
            String message = "Only initiator can cancel event.";
            log.warn("ForbiddenOperationException at EventServiceImpl.cancelEvent: {}", message);
            throw new ForbiddenOperationException(message);
        }
        if (!EventState.PENDING.equals(event.getState())) {
            String message = "Only pending events can be cancelled.";
            log.warn("ForbiddenOperationException at EventServiceImpl.cancelEvent: {}", message);
            throw new ForbiddenOperationException(message);
        }
        event.setState(EventState.CANCELED);
        Event cancelledEvent = eventRepository.save(event);
        log.info("EventServiceImpl.cancelEvent: event {} successfully cancelled", event.getId());
        return EventMapper.toEventFullDto(cancelledEvent);
    }

    @Override
    public List<EventFullDto> searchEvents(List<Long> users,
                                           List<String> states, List<Long> categories,
                                           LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                           @Min(0) int from, @Min(1) int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<EventState> eventStates = null;
        if (states != null && !states.isEmpty()) {
            eventStates = new ArrayList<>();
            for (String state : states) {
                try {
                    eventStates.add(EventState.valueOf(state));
                } catch (IllegalArgumentException e) {
                    String message = String.format("Unknown state: %s", state);
                    log.warn("IncorrectEventStateException at EventServiceImpl.searchEvents: {}",
                             message);
                    throw new IncorrectEventStateException(message);
                }
            }
        }
        if (rangeStart == null) rangeStart = LocalDateTime.now();
        return eventRepository.searchEvents(users, eventStates, categories,
                                            rangeStart, rangeEnd, pageable)
                .stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto adminUpdateEvent(Long eventId, AdminUpdateEventRequest newEvent) {
        Event event = findEventById(eventId);
        if (newEvent.getTitle() != null) event.setTitle(newEvent.getTitle());
        if (newEvent.getAnnotation() != null) event.setAnnotation(newEvent.getAnnotation());
        if (newEvent.getDescription() != null) event.setDescription(newEvent.getDescription());
        if (newEvent.getEventDate() != null) event.setEventDate(newEvent.getEventDate());
        if (newEvent.getLocation() != null) {
            event.setLocation(new Location(newEvent.getLocation().getLat(),
                                           newEvent.getLocation().getLon()));
        }
        if (newEvent.getPaid() != null) event.setPaid(newEvent.getPaid());
        if (newEvent.getCategory() != null) {
            event.setCategory(new Category(newEvent.getCategory(), null));
        }
        if (newEvent.getParticipantLimit() != null) {
            event.setParticipantLimit(newEvent.getParticipantLimit());
        }
        if (newEvent.getRequestModeration() != null) {
            event.setRequestModeration(newEvent.getRequestModeration());
        }
        Event updatedEvent = eventRepository.save(event);
        log.info("EventServiceImpl.adminUpdateEvent: event {} successfully updated", event.getId());
        return EventMapper.toEventFullDto(updatedEvent);
    }

    @Override
    public EventFullDto publishEvent(Long eventId) {
        Event event = findEventById(eventId);
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            String message = "Only events that start in more than 1 hour can be published.";
            log.warn("ForbiddenOperationException at EventServiceImpl.publishEvent: {}", message);
            throw new ForbiddenOperationException(message);
        }
        if (!EventState.PENDING.equals(event.getState())) {
            String message = "Only pending events can be published.";
            log.warn("ForbiddenOperationException at EventServiceImpl.publishEvent: {}", message);
            throw new ForbiddenOperationException(message);
        }
        event.setState(EventState.PUBLISHED);
        Event publishedEvent = eventRepository.save(event);
        log.info("EventServiceImpl.publishEvent: event {} successfully published", event.getId());
        return EventMapper.toEventFullDto(publishedEvent);
    }

    @Override
    public EventFullDto rejectEvent(Long eventId) {
        Event event = findEventById(eventId);
        if (!EventState.PENDING.equals(event.getState())) {
            String message = "Only pending events can be rejected.";
            log.warn("ForbiddenOperationException at EventServiceImpl.rejectEvent: {}", message);
            throw new ForbiddenOperationException(message);
        }
        event.setState(EventState.CANCELED);
        Event rejectedEvent = eventRepository.save(event);
        log.info("EventServiceImpl.rejectEvent: event {} successfully rejected", event.getId());
        return EventMapper.toEventFullDto(rejectedEvent);
    }

    @Override
    public EventFullDto getEventById(Long eventId) {
        Event event = findEventById(eventId);
        return EventMapper.toEventFullDto(event);
    }

    private Event findEventById(Long eventId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if (eventOptional.isEmpty()) {
            String message = String.format("Event with id=%d was not found.", eventId);
            log.warn("EntityNotFoundException at EventServiceImpl: {}", message);
            throw new EntityNotFoundException(message);
        }
        return eventOptional.get();
    }
}
