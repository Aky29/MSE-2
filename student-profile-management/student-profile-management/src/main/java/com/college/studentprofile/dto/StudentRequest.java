package com.college.studentprofile.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/** Body for POST /api/students */
public record StudentRequest(
        @NotBlank(message = "Student ID is required") String studentId,
        @NotBlank(message = "Name is required") String name,
        @NotBlank(message = "Email is required") @Email(message = "Email format is invalid") String email,
        @NotBlank(message = "Course name is required") String courseName,
        @NotNull(message = "Programming score is required")
        @DecimalMin(value = "0.0", message = "Programming score must be >= 0")
        @DecimalMax(value = "100.0", message = "Programming score must be <= 100") Double programmingScore,
        @NotNull(message = "Mathematics score is required")
        @DecimalMin(value = "0.0", message = "Mathematics score must be >= 0")
        @DecimalMax(value = "100.0", message = "Mathematics score must be <= 100") Double mathematicsScore,
        List<String> technicalSkills) {
}
