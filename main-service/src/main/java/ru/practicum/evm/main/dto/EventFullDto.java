package ru.practicum.evm.main.dto;

import lombok.Getter;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;
import ru.practicum.evm.main.model.Location;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class EventFullDto {
    private Long id;
    @NotNull
    @NotBlank
    private String title;
    @NotNull
    @NotBlank
    private String annotation;
    private String description;
    @NotNull
    private LocalDateTime eventDate;
    @NotNull
    private Location location;
    @NotNull
    private Boolean paid;
    @NotNull
    private CategoryDto category;
    @NotNull
    private UserShortDto initiator;
    private Integer participantLimit;
    private Integer confirmedRequests;
    private Boolean requestModeration;
    private String state;
    private LocalDateTime createdOn;
    private LocalDateTime publishedOn;
    private Long views;
}
