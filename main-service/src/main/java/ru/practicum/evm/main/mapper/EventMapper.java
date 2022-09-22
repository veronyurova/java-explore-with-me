package ru.practicum.evm.main.mapper;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.evm.main.service.RequestService;
import ru.practicum.evm.main.dto.*;
import ru.practicum.evm.main.model.Event;
import ru.practicum.evm.main.model.EventState;
import ru.practicum.evm.main.model.Location;
import ru.practicum.evm.main.model.Category;

import java.time.LocalDateTime;

@Component
public class EventMapper {
    private static RequestService reqService;

    @Autowired
    public EventMapper(RequestService requestService) {
        reqService = requestService;
    }

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
                reqService.getConfirmedRequests(event.getId()),
                event.getRequestModeration(),
                event.getState().toString(),
                event.getCreatedOn(),
                event.getPublishedOn(),
                0L // TODO
        );
    }

    public static EventShortDto toEventShortDto(Event event) {
        return new EventShortDto(
                event.getId(),
                event.getTitle(),
                event.getAnnotation(),
                event.getEventDate(),
                event.getPaid(),
                new CategoryDto(event.getCategory().getId(), event.getCategory().getName()),
                new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()),
                reqService.getConfirmedRequests(event.getId()),
                0L // TODO
        );
    }
}
