package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "event_similarity")
public class EventSimilarity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false, name = "event_id_a")
    private Long eventA;
    @Column(nullable = false, name = "event_id_b")
    private Long eventB;
    @Column(nullable = false)
    private Double score;
    @Column(nullable = false)
    private Instant timestamp;
}
