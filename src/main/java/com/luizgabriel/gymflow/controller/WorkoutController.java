package com.luizgabriel.gymflow.controller;

import com.luizgabriel.gymflow.domain.User;
import com.luizgabriel.gymflow.dto.request.WorkoutPostRequest;
import com.luizgabriel.gymflow.dto.request.WorkoutPutRequest;
import com.luizgabriel.gymflow.dto.response.WorkoutGetResponse;
import com.luizgabriel.gymflow.dto.response.WorkoutPostResponse;
import com.luizgabriel.gymflow.mapper.WorkoutMapper;
import com.luizgabriel.gymflow.service.WorkoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/workouts")
@RequiredArgsConstructor
@Tag(name = "Workouts", description = "Workout management")
public class WorkoutController {

    private final WorkoutService service;
    private final WorkoutMapper mapper;

    @Operation(summary = "Create a new workout")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Workout created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data or duplicate name"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public ResponseEntity<WorkoutPostResponse> save(@RequestBody @Valid WorkoutPostRequest request,
                                                    @AuthenticationPrincipal User user) {
        var savedWorkout = service.save(request, user);

        var workoutPostResponse = mapper.toWorkoutPostResponse(savedWorkout);

        return ResponseEntity.status(HttpStatus.CREATED).body(workoutPostResponse);
    }

    @Operation(summary = "Get all workouts from authenticated user",
            description = "Use `page` and `size` for pagination. Ignore the `sort` parameter.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workouts retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<Page<WorkoutGetResponse>> findAll(@PageableDefault(sort = "name", direction = Sort.Direction.ASC)
                                                            Pageable pageable,
                                                            @AuthenticationPrincipal User user) {
        var workoutsPage = service.findAll(pageable, user);

        var workoutGetResponsePage = workoutsPage.map(mapper::toWorkoutGetResponse);

        return ResponseEntity.ok(workoutGetResponsePage);
    }

    @Operation(summary = "Update a workout")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Workout updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data or duplicate name"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Workout not found")
    })
    @PutMapping
    public ResponseEntity<Void> update(@RequestBody @Valid WorkoutPutRequest request, @AuthenticationPrincipal User user) {
        service.update(request, user);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete a workout")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Workout deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Workout not found")
    })
    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
        service.delete(id, user);

        return ResponseEntity.noContent().build();
    }
}
