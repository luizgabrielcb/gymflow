package com.luizgabriel.gymflow.domain;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "workouts")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class WorkoutExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne
    private Workout workout;

    @ManyToOne
    private Exercise exercise;

    private Integer sets;

    private Integer reps;
}
