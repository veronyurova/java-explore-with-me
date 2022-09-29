package ru.practicum.evm.main.dto;

import lombok.Getter;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDto {
    private Long id;
    private Long user;
    private Long event;
    private String review;
    private Boolean positive;
    private String status;
}
