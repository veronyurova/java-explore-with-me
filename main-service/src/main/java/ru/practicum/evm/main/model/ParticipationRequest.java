package ru.practicum.evm.main.model;

import lombok.*;

import javax.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "participation_requests")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "event_id")
    private Long event;
    @Column(name = "requester_id")
    private Long requester;
    private LocalDateTime created;
    private RequestStatus status;
}
