package com.college.studentprofile;

import com.college.studentprofile.model.Student;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StudentTest {

    private Student make(double p, double m) {
        return new Student("S1", "Test", "t@x.com", "CSE", p, m, List.of("Java"));
    }

    @Test
    void averageAndCategoryBoundaries() {
        assertEquals(85.0, make(85, 85).getAverageScore());
        assertEquals("Excellent", make(85, 85).getPerformanceCategory());
        assertEquals("Good", make(84, 85).getPerformanceCategory());      // 84.5
        assertEquals("Good", make(70, 70).getPerformanceCategory());
        assertEquals("Satisfactory", make(50, 69).getPerformanceCategory()); // 59.5
        assertEquals("Needs Improvement", make(49, 50).getPerformanceCategory()); // 49.5
    }

    @Test
    void strongerSubject() {
        assertEquals("Programming", make(90, 80).getStrongerSubject());
        assertEquals("Mathematics", make(60, 80).getStrongerSubject());
        assertEquals("Both Equal", make(70, 70).getStrongerSubject());
    }

    @Test
    void scoresMustBeBetween0And100() {
        assertThrows(IllegalArgumentException.class, () -> make(101, 50));
        assertThrows(IllegalArgumentException.class, () -> make(50, -1));
    }
}
