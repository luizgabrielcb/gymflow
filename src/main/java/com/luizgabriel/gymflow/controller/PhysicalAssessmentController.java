package com.luizgabriel.gymflow.controller;

import com.luizgabriel.gymflow.domain.User;
import com.luizgabriel.gymflow.dto.request.PhysicalAssessmentPostRequest;
import com.luizgabriel.gymflow.dto.response.PhysicalAssessmentGetResponse;
import com.luizgabriel.gymflow.dto.response.PhysicalAssessmentPostResponse;
import com.luizgabriel.gymflow.mapper.PhysicalAssessmentMapper;
import com.luizgabriel.gymflow.service.PhysicalAssessmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/physical-assessments")
@RequiredArgsConstructor
public class PhysicalAssessmentController {

    private final PhysicalAssessmentService service;
    private final PhysicalAssessmentMapper mapper;

    @PostMapping
    public ResponseEntity<PhysicalAssessmentPostResponse> save(@RequestBody @Valid PhysicalAssessmentPostRequest request,
                                                               @AuthenticationPrincipal User user) {
        var savedAssessment = service.save(request, user);

        var physicalAssessmentPostResponse = mapper.toPhysicalAssessmentPostResponse(savedAssessment);

        return ResponseEntity.status(HttpStatus.CREATED).body(physicalAssessmentPostResponse);
    }

    @GetMapping
    public ResponseEntity<List<PhysicalAssessmentGetResponse>> findByAuthenticatedUser(@AuthenticationPrincipal User user) {
        var physicalAssessmentsList = service.findByAuthenticatedUser(user);

        var physicalAssessmentGetResponseList = mapper.toPhysicalAssessmentGetResponseList(physicalAssessmentsList);

        return ResponseEntity.ok(physicalAssessmentGetResponseList);
    }

    @GetMapping("{id}")
    public ResponseEntity<PhysicalAssessmentGetResponse> findById(@PathVariable Long id) {
        var physicalAssessments = service.findByIdOrThrowNotFound(id);

        var physicalAssessmentGetResponse = mapper.toPhysicalAssessmentGetResponse(physicalAssessments);

        return ResponseEntity.ok(physicalAssessmentGetResponse);
    }
}
