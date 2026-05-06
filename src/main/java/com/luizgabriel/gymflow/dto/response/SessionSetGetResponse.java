package com.luizgabriel.gymflow.dto.response;

import java.math.BigDecimal;

public record SessionSetGetResponse(Long id,
                                    String exerciseName,
                                    Integer setNumber,
                                    Integer repsNumber,
                                    BigDecimal weightKg,
                                    Integer restSeconds) {
}
