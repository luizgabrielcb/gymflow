package com.luizgabriel.gymflow.dto.response;

import java.math.BigDecimal;

public record PhysicalAssessmentGetResponse(Long id,
                                            BigDecimal weight,
                                            BigDecimal height,
                                            BigDecimal fatPercentage) {
}
