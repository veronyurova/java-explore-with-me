package ru.practicum.evm.main.dto;

import lombok.Getter;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class UserDto {
    private Long id;
    @Email
    @NotNull
    @NotBlank
    private String email;
    @NotNull
    @NotBlank
    @Size(min = 3, max = 120)
    private String name;
}
