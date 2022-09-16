package ru.practicum.evm.main.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "compilations")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private Boolean pinned;
}
