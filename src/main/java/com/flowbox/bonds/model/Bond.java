package com.flowbox.bonds.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

import com.flowbox.bonds.model.User;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bond {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @Positive
    private double nominalValue;

    @PositiveOrZero
    private double couponRate;

    @Min(1)
    private int maturityPeriods;

    @Min(1)
    private int frequency;

    @PositiveOrZero
    private double marketRate;

    @Min(0)
    private int gracePeriods;

    @NotBlank
    private String graceType;

    @NotBlank
    private String currency;

    @NotBlank
    private String interestType;

    @Min(1)
    private int capitalization;

    private LocalDate createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
