package ru.practicum.evm.main.service;

import ru.practicum.evm.main.dto.EventFullDto;
import ru.practicum.evm.main.dto.NewEventDto;
import ru.practicum.evm.main.dto.UpdateEventRequest;

import javax.validation.constraints.Min;
import javax.validation.Valid;
import java.util.List;

public interface EventService {
    List<EventFullDto> getUserEvents(Long userId, @Min(0) int from, @Min(1) int size);

    EventFullDto addEvent(Long userId, @Valid NewEventDto newEventDto);

    EventFullDto updateEvent(Long userId, @Valid UpdateEventRequest newEvent);

    EventFullDto getEventById(Long userId, Long eventId);

    EventFullDto cancelEvent(Long userId, Long eventId);
}
