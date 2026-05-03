package com.luizgabriel.gymflow.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "session_sets")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SessionSet {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private TrainingSession trainingSession;

    @ManyToOne(fetch = FetchType.LAZY)
    private Exercise exercise;

    @Setter
    @Column(nullable = false)
    private Integer setNumber;

    @Setter
    @Column(nullable = false)
    private Integer repsNumber;

    @Setter
    @Column(nullable = false)
    private BigDecimal weightKg;

    @Setter
    @Column(nullable = false)
    private Integer restSeconds;
}
