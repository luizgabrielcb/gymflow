package com.luizgabriel.gymflow.mapper;

import com.luizgabriel.gymflow.domain.Exercise;
import com.luizgabriel.gymflow.dto.request.ExercisePostRequest;
import com.luizgabriel.gymflow.dto.response.ExerciseGetResponse;
import com.luizgabriel.gymflow.dto.response.ExercisePostResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ExerciseMapper {

    List<ExerciseGetResponse> toExerciseGetResponseList(List<Exercise> exerciseList);

    Exercise toExercise(ExercisePostRequest exercisePostRequest);

    ExercisePostResponse toExercisePostResponse(Exercise exercise);
}
