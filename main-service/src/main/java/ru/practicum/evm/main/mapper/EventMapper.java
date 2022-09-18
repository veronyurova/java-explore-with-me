package ru.practicum.evm.main.mapper;

import ru.practicum.evm.main.dto.*;
import ru.practicum.evm.main.model.Event;
import ru.practicum.evm.main.model.EventState;
import ru.practicum.evm.main.model.Location;
import ru.practicum.evm.main.model.Category;

import java.time.LocalDateTime;

public class EventMapper {
    public static Event toEventAdd(NewEventDto newEventDto) {
        return new Event(
                null,
                newEventDto.getTitle(),
                newEventDto.getAnnotation(),
                newEventDto.getDescription(),
                newEventDto.getEventDate(),
                new Location(
                        newEventDto.getLocation().getLat(),
                        newEventDto.getLocation().getLon()
                ),
                newEventDto.getPaid(),
                new Category(newEventDto.getCategory(), null),
                null,
                newEventDto.getParticipantLimit(),
                newEventDto.getRequestModeration(),
                EventState.PENDING,
                LocalDateTime.now(),
                null
        );
    }

    public static EventFullDto toEventFullDto(Event event) {
        return new EventFullDto(
                event.getId(),
                event.getTitle(),
                event.getAnnotation(),
                event.getDescription(),
                event.getEventDate(),
                event.getLocation(),
                event.getPaid(),
                new CategoryDto(event.getCategory().getId(), event.getCategory().getName()),
                new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()),
                event.getParticipantLimit(),
                0, //TODO
                event.getRequestModeration(),
                event.getState().toString(),
                event.getCreatedOn(),
                event.getPublishedOn(),
                0L //TODO
        );
    }
}
