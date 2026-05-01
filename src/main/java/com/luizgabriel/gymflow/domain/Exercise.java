package com.luizgabriel.gymflow.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Table(name = "exercises")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String muscleGroup;

    @OneToMany(mappedBy = "exercise")
    private List<WorkoutExercise> workoutExercises;
}
