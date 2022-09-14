package ru.practicum.evm.main.dto;

import lombok.Getter;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class ParticipationRequestDto {
    private Long id;
    private Long event;
    private Long requester;
    private LocalDateTime created;
    private String status;
}
