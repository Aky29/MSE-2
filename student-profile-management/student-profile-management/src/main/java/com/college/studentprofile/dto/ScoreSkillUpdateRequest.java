package com.college.studentprofile.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

import java.util.List;

/** Body for PUT /api/students/{id}/scores-skills. Any field left out is not changed. */
public record ScoreSkillUpdateRequest(
        @DecimalMin(value = "0.0", message = "Programming score must be >= 0")
        @DecimalMax(value = "100.0", message = "Programming score must be <= 100") Double programmingScore,
        @DecimalMin(value = "0.0", message = "Mathematics score must be >= 0")
        @DecimalMax(value = "100.0", message = "Mathematics score must be <= 100") Double mathematicsScore,
        List<String> technicalSkills) {
}
