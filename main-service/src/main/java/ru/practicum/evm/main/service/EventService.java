package ru.practicum.evm.main.service;

import ru.practicum.evm.main.dto.*;

import javax.validation.constraints.Min;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventFullDto> getEvents(String text, List<Long> categories, Boolean paid,
                                 LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                 Boolean onlyAvailable, String sort,
                                 @Min(0) int from, @Min(1) int size);

    EventFullDto getPublishedEventById(Long eventId);

    List<EventFullDto> getUserEvents(Long userId, @Min(0) int from, @Min(1) int size);

    EventFullDto addEvent(Long userId, @Valid NewEventDto newEventDto);

    EventFullDto updateEvent(Long userId, @Valid UpdateEventRequest newEvent);

    EventFullDto getUserEventById(Long userId, Long eventId);

    EventFullDto cancelEvent(Long userId, Long eventId);

    List<EventFullDto> searchEvents(List<Long> users,
                                    List<String> states, List<Long> categories,
                                    LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                    @Min(0) int from, @Min(1) int size);

    EventFullDto adminUpdateEvent(Long eventId, AdminUpdateEventRequest newEvent);

    EventFullDto publishEvent(Long eventId);

    EventFullDto rejectEvent(Long eventId);

    EventFullDto getEventById(Long eventId);

    List<EventShortDto> getEventsByIds(List<Long> ids);
}
