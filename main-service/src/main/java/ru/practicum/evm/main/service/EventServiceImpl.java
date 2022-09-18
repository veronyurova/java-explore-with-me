package ru.practicum.evm.main.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import ru.practicum.evm.main.model.Category;
import ru.practicum.evm.main.repository.EventRepository;
import ru.practicum.evm.main.mapper.EventMapper;
import ru.practicum.evm.main.dto.EventFullDto;
import ru.practicum.evm.main.dto.NewEventDto;
import ru.practicum.evm.main.dto.UpdateEventRequest;
import ru.practicum.evm.main.model.User;
import ru.practicum.evm.main.model.Event;
import ru.practicum.evm.main.model.EventState;
import ru.practicum.evm.main.exception.ForbiddenOperationException;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.Min;
import javax.validation.Valid;
import java.util.List;
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
        return eventRepository.findAllByInitiatorId(userId, PageRequest.of(from / size, size))
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
        boolean isUpdated = false;
        if (newEvent.getTitle() != null) {
            event.setTitle(newEvent.getTitle());
            isUpdated = true;
        }
        if (newEvent.getAnnotation() != null) {
            event.setAnnotation(newEvent.getAnnotation());
            isUpdated = true;
        }
        if (newEvent.getDescription() != null) {
            event.setDescription(newEvent.getDescription());
            isUpdated = true;
        }
        if (newEvent.getEventDate() != null) {
            event.setEventDate(newEvent.getEventDate());
            isUpdated = true;
        }
        if (newEvent.getPaid() != null) {
            event.setPaid(newEvent.getPaid());
            isUpdated = true;
        }
        if (newEvent.getCategory() != null) {
            event.setCategory(new Category(newEvent.getCategory(), null));
            isUpdated = true;
        }
        if (newEvent.getParticipantLimit() != null) {
            event.setParticipantLimit(newEvent.getParticipantLimit());
            isUpdated = true;
        }
        if (isUpdated) event.setState(EventState.PENDING);
        Event updatedEvent = eventRepository.save(event);
        log.info("EventServiceImpl.updateEvent: event {} successfully updated", event.getId());
        return EventMapper.toEventFullDto(updatedEvent);
    }

    @Override
    public EventFullDto getEventById(Long userId, Long eventId) {
        Event event = findEventById(eventId);
        if (!userId.equals(event.getInitiator().getId())) {
            String message = "Only initiator can view full event.";
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
