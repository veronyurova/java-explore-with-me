package ru.practicum.evm.main.dto;

import lombok.Getter;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class UserShortDto {
    private Long id;
    private String name;
}
