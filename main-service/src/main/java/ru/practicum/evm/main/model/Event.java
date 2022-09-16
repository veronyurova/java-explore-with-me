package ru.practicum.evm.main.model;

import lombok.*;

import javax.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String annotation;
    private String description;
    @Column(name = "event_date")
    private LocalDateTime eventDate;
    @AttributeOverrides({
            @AttributeOverride(name = "lat", column = @Column(name = "location_lat")),
            @AttributeOverride(name = "lon", column = @Column(name = "location_lon"))
    })
    private Location location;
    private Boolean paid;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;
    @Column(name = "participant_limit")
    private Integer participantLimit;
    @Column(name = "request_moderation")
    private Boolean requestModeration;
    private EventState state;
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    @Column(name = "published_on")
    private LocalDateTime publishedOn;
}
