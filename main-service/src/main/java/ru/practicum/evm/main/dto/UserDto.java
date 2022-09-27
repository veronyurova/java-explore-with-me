package ru.practicum.evm.main.dto;

import lombok.Getter;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String name;
}
