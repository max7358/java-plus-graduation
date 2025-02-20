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
@Table(name = "events_similarity")
public class EventSimilarity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long eventA;
    private Long eventB;
    private Double score;
    private Instant timestamp;
}
