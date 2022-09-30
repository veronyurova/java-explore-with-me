package ru.practicum.evm.main.dto;

import lombok.*;

@Getter
@ToString
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
