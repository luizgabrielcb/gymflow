package com.luizgabriel.gymflow.mapper;

import com.luizgabriel.gymflow.domain.SessionSet;
import com.luizgabriel.gymflow.domain.TrainingSession;
import com.luizgabriel.gymflow.dto.response.SessionSetGetResponse;
import com.luizgabriel.gymflow.dto.response.SessionSetIdPostResponse;
import com.luizgabriel.gymflow.dto.response.TrainingSessionGetResponse;
import com.luizgabriel.gymflow.dto.response.TrainingSessionIdPostResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TrainingSessionMapper {

    @Mapping(target = "id", source = "trainingSession.id")
    TrainingSessionIdPostResponse toTrainingSessionIdPostResponse(TrainingSession trainingSession);

    @Mapping(target = "id", source = "sessionSet.id")
    SessionSetIdPostResponse toSessionSetIdPostResponse(SessionSet sessionSet);

    @Mapping(target = "workoutName", source = "workout.name")
    @Mapping(target = "durationMinutes", source = "duration")
    TrainingSessionGetResponse toTrainingSessionGetResponse(TrainingSession trainingSession);

    @Mapping(target = "exerciseName", source = "exercise.name")
    SessionSetGetResponse toSessionSetGetResponse(SessionSet sessionSet);

    List<TrainingSessionGetResponse> toTrainingSessionGetResponseList(List<TrainingSession> trainingSessionList);
}
