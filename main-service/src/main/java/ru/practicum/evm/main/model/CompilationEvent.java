package ru.practicum.evm.main.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "compilation_event")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CompilationEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "compilation_id")
    private Long compilation;
    @Column(name = "event_id")
    private Long event;
}
