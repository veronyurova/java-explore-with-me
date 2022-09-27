package ru.practicum.ewm.stats.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.ewm.stats.service.StatsService;
import ru.practicum.ewm.stats.model.EndpointHit;
import ru.practicum.ewm.stats.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class StatsController {
    private final StatsService statsService;

    @Autowired
    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @PostMapping("/hit")
    public void saveHit(@RequestBody EndpointHit endpointHit) {
        statsService.saveHit(endpointHit);
    }

    @GetMapping("/stats")
    public ViewStats getStats(@RequestParam
                              @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                              LocalDateTime start,
                              @RequestParam
                              @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                              LocalDateTime end,
                              @RequestParam(required = false) List<String> uris,
                              @RequestParam(defaultValue = "false") Boolean unique) {
        return statsService.getStats(start, end, uris, unique);
    }
}
