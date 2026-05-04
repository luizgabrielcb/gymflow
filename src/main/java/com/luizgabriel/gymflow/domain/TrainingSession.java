package com.luizgabriel.gymflow.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Table(name = "training_sessions")
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TrainingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_id", nullable = false)
    private Workout workout;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant startedAt;

    @Setter
    private Instant finishedAt;

    @Setter
    private Integer duration;
}
