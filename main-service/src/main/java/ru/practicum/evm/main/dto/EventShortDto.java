package ru.practicum.evm.main.dto;

import lombok.Getter;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class EventShortDto {
    private Long id;
    private String title;
    private String annotation;
    private LocalDateTime eventDate;
    private Boolean paid;
    private CategoryDto category;
    private UserShortDto initiator;
    private Integer confirmedRequests;
    private Long views;
}
