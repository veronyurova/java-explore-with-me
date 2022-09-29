package ru.practicum.evm.main.dto;

import lombok.Getter;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class NewReviewDto {
    @NotNull
    private Long event;
    @Size(max = 7000)
    private String review;
    @NotNull
    private Boolean positive;
}
