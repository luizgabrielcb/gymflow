package com.luizgabriel.gymflow.mapper;

import com.luizgabriel.gymflow.domain.PhysicalAssessment;
import com.luizgabriel.gymflow.dto.response.PhysicalAssessmentGetResponse;
import com.luizgabriel.gymflow.dto.response.PhysicalAssessmentPostResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PhysicalAssessmentMapper {

    PhysicalAssessmentPostResponse toPhysicalAssessmentPostResponse(PhysicalAssessment PhysicalAssessment);

    PhysicalAssessmentGetResponse toPhysicalAssessmentGetResponse(PhysicalAssessment PhysicalAssessment);

    List<PhysicalAssessmentGetResponse> toPhysicalAssessmentGetResponseList(List<PhysicalAssessment> PhysicalAssessmentList);
}
