package com.luizgabriel.gymflow.mapper;

import com.luizgabriel.gymflow.domain.SessionSet;
import com.luizgabriel.gymflow.domain.TrainingSession;
import com.luizgabriel.gymflow.dto.response.SessionSetIdPostResponse;
import com.luizgabriel.gymflow.dto.response.TrainingSessionGetResponse;
import com.luizgabriel.gymflow.dto.response.TrainingSessionIdPostResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TrainingSessionMapper {

    TrainingSessionIdPostResponse toTrainingSessionIdPostResponse(TrainingSession trainingSession);

    SessionSetIdPostResponse toSessionSetIdPostResponse(SessionSet sessionSet);

    List<TrainingSessionGetResponse> toTrainingSessionGetResponseList(List<TrainingSession> trainingSessionList);

   TrainingSessionGetResponse toTrainingSessionGetResponse(TrainingSession trainingSession);
}
