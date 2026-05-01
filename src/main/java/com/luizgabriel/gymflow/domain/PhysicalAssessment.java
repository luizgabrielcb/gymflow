package com.luizgabriel.gymflow.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Table(name = "physical_assessments")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PhysicalAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    @Column(nullable = false)
    private BigDecimal weight;
    @Column(nullable = false)
    private BigDecimal height;
    @Column(nullable = false)
    private BigDecimal fatPercentage;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @CreationTimestamp
    private Instant createdAt;
}
