package ru.practicum.evm.main.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class NewReviewDto {
    @NotNull
    private Long event;
    @Size(max = 7000)
    private String review;
    @NotNull
    private Boolean positive;
}
