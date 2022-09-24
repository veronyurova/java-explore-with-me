package ru.practicum.evm.main.mapper;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.evm.main.service.RequestService;
import ru.practicum.evm.main.client.StatsClient;
import org.springframework.http.ResponseEntity;
import ru.practicum.evm.main.model.*;
import ru.practicum.evm.main.dto.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;

@Component
public class EventMapper {
    private static RequestService reqService;
    private static StatsClient client;

    @Autowired
    public EventMapper(RequestService requestService,
                       StatsClient statsClient) {
        reqService = requestService;
        client = statsClient;
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
                getViews(event.getId())
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
                getViews(event.getId())
        );
    }

    public static EventShortDto toEventShortFromFull(EventFullDto event) {
        return new EventShortDto(
                event.getId(),
                event.getTitle(),
                event.getAnnotation(),
                event.getEventDate(),
                event.getPaid(),
                event.getCategory(),
                event.getInitiator(),
                event.getConfirmedRequests(),
                event.getViews()
        );
    }

    public static Long getViews(Long eventId) {
        ResponseEntity<Object> responseEntity = client.getStats(
                LocalDateTime.of(2022, 9, 1, 0, 0),
                LocalDateTime.now(),
                List.of("/events/" + eventId),
                false);
        Integer hits = (Integer) ((LinkedHashMap) responseEntity.getBody()).get("hits");
        return Long.valueOf(hits);
    }
}
