package com.luizgabriel.gymflow.mapper;

import com.luizgabriel.gymflow.domain.Workout;
import com.luizgabriel.gymflow.domain.WorkoutExercise;
import com.luizgabriel.gymflow.dto.response.WorkoutExerciseResponse;
import com.luizgabriel.gymflow.dto.response.WorkoutGetResponse;
import com.luizgabriel.gymflow.dto.response.WorkoutPostResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WorkoutMapper {

    @Mapping(target = "exercises", source = "workoutExercises")
    WorkoutPostResponse toWorkoutPostResponse(Workout workout);

    @Mapping(target = "id", source = "exercise.id")
    @Mapping(target = "name", source = "exercise.name")
    @Mapping(target = "muscleGroup", source = "exercise.muscleGroup")
    WorkoutExerciseResponse toWorkoutExerciseResponse(WorkoutExercise workoutExercise);

    @Mapping(target = "exercises", source = "workoutExercises")
    WorkoutGetResponse toWorkoutGetResponse(Workout workout);
}
