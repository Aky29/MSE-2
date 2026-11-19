package com.college.studentprofile.dto;

/** One row of the merit list. */
public record MeritListEntry(int rank, String studentId, String name, String courseName,
                             double averageScore, String performanceCategory) {
}
