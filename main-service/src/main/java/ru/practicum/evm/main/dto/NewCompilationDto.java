package ru.practicum.evm.main.dto;

import lombok.Getter;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class NewCompilationDto {
    @NotNull
    @NotBlank
    @Size(min = 3, max = 120)
    private String title;
    private Boolean pinned;
    private Set<Long> events;
}
