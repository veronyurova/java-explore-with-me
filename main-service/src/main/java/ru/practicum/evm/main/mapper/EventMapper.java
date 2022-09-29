package ru.practicum.evm.main.mapper;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.evm.main.repository.RequestRepository;
import ru.practicum.evm.main.client.StatsClient;
import org.springframework.http.ResponseEntity;
import ru.practicum.evm.main.model.*;
import ru.practicum.evm.main.dto.*;
import ru.practicum.evm.main.repository.ReviewRepository;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;

@Component
public class EventMapper {
    private final StatsClient statsClient;
    private final RequestRepository requestRepository;
    private final ReviewRepository reviewRepository;

    @Autowired
    public EventMapper(StatsClient statsClient,
                       RequestRepository requestRepository,
                       ReviewRepository reviewRepository) {
        this.statsClient = statsClient;
        this.requestRepository = requestRepository;
        this.reviewRepository = reviewRepository;
    }

    public Event toEventAdd(NewEventDto newEventDto) {
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

    public EventFullDto toEventFullDto(Event event) {
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
                requestRepository.countByEventAndStatus(event.getId(), RequestStatus.CONFIRMED),
                event.getRequestModeration(),
                event.getState().toString(),
                event.getCreatedOn(),
                event.getPublishedOn(),
                getViews(event.getId()),
                reviewRepository.countEventRating(event.getId())
        );
    }

    public EventShortDto toEventShortDto(Event event) {
        return new EventShortDto(
                event.getId(),
                event.getTitle(),
                event.getAnnotation(),
                event.getEventDate(),
                event.getPaid(),
                new CategoryDto(event.getCategory().getId(), event.getCategory().getName()),
                new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()),
                requestRepository.countByEventAndStatus(event.getId(), RequestStatus.CONFIRMED),
                getViews(event.getId()),
                reviewRepository.countEventRating(event.getId())
        );
    }

    public EventShortDto toEventShortFromFull(EventFullDto event) {
        return new EventShortDto(
                event.getId(),
                event.getTitle(),
                event.getAnnotation(),
                event.getEventDate(),
                event.getPaid(),
                event.getCategory(),
                event.getInitiator(),
                event.getConfirmedRequests(),
                event.getViews(),
                event.getRating()
        );
    }

    public Long getViews(Long eventId) {
        ResponseEntity<Object> responseEntity = statsClient.getStats(
                LocalDateTime.of(2022, 9, 1, 0, 0),
                LocalDateTime.now(),
                List.of("/events/" + eventId),
                false
        );
        Integer hits = (Integer) ((LinkedHashMap) responseEntity.getBody()).get("hits");
        return Long.valueOf(hits);
    }
}
