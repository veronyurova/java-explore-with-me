package ru.practicum.evm.main.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long user;
    @Column(name = "event_id")
    private Long event;
    private String review;
    private Boolean positive;
    @Enumerated(EnumType.STRING)
    private ReviewStatus status;
}
