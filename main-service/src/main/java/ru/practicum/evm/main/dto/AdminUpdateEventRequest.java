package ru.practicum.evm.main.dto;

import lombok.Getter;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;
import ru.practicum.evm.main.model.Location;

import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class AdminUpdateEventRequest {
    private String title;
    private String annotation;
    private String description;
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    private Integer category;
    private Integer participantLimit;
    private Boolean requestModeration;
}
