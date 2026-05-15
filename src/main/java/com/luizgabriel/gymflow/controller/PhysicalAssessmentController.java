package com.luizgabriel.gymflow.controller;

import com.luizgabriel.gymflow.domain.User;
import com.luizgabriel.gymflow.dto.request.PhysicalAssessmentPostRequest;
import com.luizgabriel.gymflow.dto.request.PhysicalAssessmentPutRequest;
import com.luizgabriel.gymflow.dto.response.PhysicalAssessmentGetResponse;
import com.luizgabriel.gymflow.dto.response.PhysicalAssessmentPostResponse;
import com.luizgabriel.gymflow.mapper.PhysicalAssessmentMapper;
import com.luizgabriel.gymflow.service.PhysicalAssessmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/physical-assessments")
@RequiredArgsConstructor
@Tag(name = "Physical Assessments", description = "Physical assessment management")
public class PhysicalAssessmentController {

    private final PhysicalAssessmentService service;
    private final PhysicalAssessmentMapper mapper;

    @Operation(summary = "Create a new physical assessment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Physical assessment created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping
    public ResponseEntity<PhysicalAssessmentPostResponse> save(@RequestBody @Valid PhysicalAssessmentPostRequest request) {
        var savedAssessment = service.save(request);

        var physicalAssessmentPostResponse = mapper.toPhysicalAssessmentPostResponse(savedAssessment);

        return ResponseEntity.status(HttpStatus.CREATED).body(physicalAssessmentPostResponse);
    }

    @Operation(summary = "Get all assessments from authenticated user",
            description = "Use `page` and `size` for pagination. Ignore the `sort` parameter.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Assessments retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<Page<PhysicalAssessmentGetResponse>> findByAuthenticatedUser(@ParameterObject @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
                                                                                           Pageable pageable,
                                                                                       @AuthenticationPrincipal User user) {
        var physicalAssessmentsPage = service.findByAuthenticatedUser(pageable, user);

        var physicalAssessmentGetResponsePage = physicalAssessmentsPage.map(mapper::toPhysicalAssessmentGetResponse);

        return ResponseEntity.ok(physicalAssessmentGetResponsePage);
    }

    @Operation(summary = "Get physical assessment by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Assessment retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Assessment not found")
    })
    @GetMapping("{id}")
    public ResponseEntity<PhysicalAssessmentGetResponse> findById(@PathVariable Long id) {
        var physicalAssessments = service.findById(id);

        var physicalAssessmentGetResponse = mapper.toPhysicalAssessmentGetResponse(physicalAssessments);

        return ResponseEntity.ok(physicalAssessmentGetResponse);
    }

    @Operation(summary = "Get all assessments by user ID",
            description = "Use `page` and `size` for pagination. Ignore the `sort` parameter.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Assessments retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("user/{id}")
    public ResponseEntity<Page<PhysicalAssessmentGetResponse>> findUserAssessmentByUserId(@ParameterObject @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
                                                                                          Pageable pageable,
                                                                                          @PathVariable Long id) {
        var physicalAssessmentsPage = service.findUserAssessmentByUserId(pageable, id);

        var physicalAssessmentGetResponsePage = physicalAssessmentsPage.map(mapper::toPhysicalAssessmentGetResponse);

        return ResponseEntity.ok(physicalAssessmentGetResponsePage);
    }

    @Operation(summary = "Update a physical assessment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Assessment updated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Assessment not found")
    })
    @PutMapping
    public ResponseEntity<Void> update(@RequestBody @Valid PhysicalAssessmentPutRequest request) {
        service.update(request);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete a physical assessment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Assessment deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Assessment not found")
    })
    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}
