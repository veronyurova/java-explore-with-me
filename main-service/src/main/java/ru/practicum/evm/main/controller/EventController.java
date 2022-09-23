package ru.practicum.evm.main.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.evm.main.service.EventService;
import ru.practicum.evm.main.client.StatsClient;
import ru.practicum.evm.main.model.EndpointHit;
import ru.practicum.evm.main.dto.EventFullDto;
import ru.practicum.evm.main.dto.NewEventDto;
import ru.practicum.evm.main.dto.UpdateEventRequest;
import ru.practicum.evm.main.dto.AdminUpdateEventRequest;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class EventController {
    private final EventService eventService;
    private final StatsClient statsClient;

    @Autowired
    public EventController(EventService eventService, StatsClient statsClient) {
        this.eventService = eventService;
        this.statsClient = statsClient;
    }

    @GetMapping("/events")
    public List<EventFullDto> getEvents(@RequestParam(required = false) String text,
                                        @RequestParam(required = false) List<Long> categories,
                                        @RequestParam(required = false) Boolean paid,
                                        @RequestParam(required = false)
                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                        LocalDateTime rangeStart,
                                        @RequestParam(required = false)
                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                        LocalDateTime rangeEnd,
                                        @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                        @RequestParam(required = false) String sort,
                                        @RequestParam(defaultValue = "0") int from,
                                        @RequestParam(defaultValue = "10") int size,
                                        HttpServletRequest request) {
        EndpointHit endpointHit = new EndpointHit(
                null,
                "explore-with-me",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()
        );
        statsClient.saveHit(endpointHit);
        return eventService.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable,
                                      sort, from, size);
    }

    @GetMapping("/events/{id}")
    public EventFullDto getPublishedEventById(@PathVariable Long id, HttpServletRequest request) {
        EndpointHit endpointHit = new EndpointHit(
                null,
                "explore-with-me",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()
        );
        statsClient.saveHit(endpointHit);
        return eventService.getPublishedEventById(id);
    }

    @GetMapping("/users/{userId}/events")
    public List<EventFullDto> getUserEvents(@PathVariable Long userId,
                                            @RequestParam(defaultValue = "0") int from,
                                            @RequestParam(defaultValue = "10") int size) {
        return eventService.getUserEvents(userId, from, size);
    }

    @PostMapping("/users/{userId}/events")
    public EventFullDto addEvent(@PathVariable Long userId,
                                 @RequestBody NewEventDto newEventDto) {
        return eventService.addEvent(userId, newEventDto);
    }

    @PatchMapping("/users/{userId}/events")
    public EventFullDto updateEvent(@PathVariable Long userId,
                                    @RequestBody UpdateEventRequest newEvent) {
        return eventService.updateEvent(userId, newEvent);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public EventFullDto getUserEventById(@PathVariable Long userId,
                                         @PathVariable Long eventId) {
        return eventService.getUserEventById(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventFullDto cancelEvent(@PathVariable Long userId,
                                    @PathVariable Long eventId) {
        return eventService.cancelEvent(userId, eventId);
    }

    @GetMapping("admin/events")
    public List<EventFullDto> searchEvents(@RequestParam(required = false) List<Long> users,
                                           @RequestParam(required = false) List<String> states,
                                           @RequestParam(required = false) List<Long> categories,
                                           @RequestParam(required = false)
                                           @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                           LocalDateTime rangeStart,
                                           @RequestParam(required = false)
                                           @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                           LocalDateTime rangeEnd,
                                           @RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "10") int size) {
        return eventService.searchEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PutMapping("admin/events/{eventId}")
    public EventFullDto adminUpdateEvent(@PathVariable Long eventId,
                                         @RequestBody AdminUpdateEventRequest newEvent) {
        return eventService.adminUpdateEvent(eventId, newEvent);
    }

    @PatchMapping("admin/events/{eventId}/publish")
    public EventFullDto publishEvent(@PathVariable Long eventId) {
        return eventService.publishEvent(eventId);
    }

    @PatchMapping("admin/events/{eventId}/reject")
    public EventFullDto rejectEvent(@PathVariable Long eventId) {
        return eventService.rejectEvent(eventId);
    }
}
