package com.college.studentprofile.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Q1 - Student class.
 * Encapsulation: all fields private, access only via getters/setters.
 * Setters validate data (scores must be 0..100) so an invalid object can never exist.
 * Also a JPA entity mapped to tables `students` and `student_skills`.
 */
@Entity
@Table(name = "students")
public class Student {

    // Category boundaries (business rules)
    public static final double EXCELLENT_MIN = 85.0;
    public static final double GOOD_MIN = 70.0;
    public static final double SATISFACTORY_MIN = 50.0;

    @Id
    @Column(name = "student_id", length = 20, nullable = false)
    private String studentId;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "email", length = 150, nullable = false, unique = true)
    private String email;

    @Column(name = "course_name", length = 100, nullable = false)
    private String courseName;

    @Column(name = "programming_score", nullable = false)
    private Double programmingScore;

    @Column(name = "mathematics_score", nullable = false)
    private Double mathematicsScore;

    // List of technical skills -> separate table student_skills (one row per skill)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "student_skills", joinColumns = @JoinColumn(name = "student_id"))
    @Column(name = "skill", length = 50, nullable = false)
    private List<String> technicalSkills = new ArrayList<>();

    // ---------- Constructors ----------
    /** Required by JPA. */
    protected Student() {
    }

    public Student(String studentId, String name, String email, String courseName,
                   Double programmingScore, Double mathematicsScore, List<String> technicalSkills) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.courseName = courseName;
        setProgrammingScore(programmingScore);
        setMathematicsScore(mathematicsScore);
        setTechnicalSkills(technicalSkills);
    }

    // ---------- Getters & Setters ----------
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public Double getProgrammingScore() { return programmingScore; }
    public void setProgrammingScore(Double programmingScore) {
        this.programmingScore = validateScore(programmingScore, "Programming score");
    }

    public Double getMathematicsScore() { return mathematicsScore; }
    public void setMathematicsScore(Double mathematicsScore) {
        this.mathematicsScore = validateScore(mathematicsScore, "Mathematics score");
    }

    public List<String> getTechnicalSkills() { return technicalSkills; }
    public void setTechnicalSkills(List<String> skills) {
        List<String> copy = (skills == null) ? new ArrayList<>() : new ArrayList<>(skills);
        this.technicalSkills.clear();
        this.technicalSkills.addAll(copy);
    }

    // ---------- Business methods (derived, NOT stored in DB) ----------

    /** Average of programming and mathematics, rounded to 2 decimals. */
    public double getAverageScore() {
        double avg = (programmingScore + mathematicsScore) / 2.0;
        return Math.round(avg * 100.0) / 100.0;
    }

    /** >=85 Excellent, 70-84.99 Good, 50-69.99 Satisfactory, <50 Needs Improvement. */
    public String getPerformanceCategory() {
        double avg = getAverageScore();
        if (avg >= EXCELLENT_MIN) return "Excellent";
        if (avg >= GOOD_MIN) return "Good";
        if (avg >= SATISFACTORY_MIN) return "Satisfactory";
        return "Needs Improvement";
    }

    /** Subject in which the student scored higher. */
    public String getStrongerSubject() {
        int cmp = Double.compare(programmingScore, mathematicsScore);
        if (cmp > 0) return "Programming";
        if (cmp < 0) return "Mathematics";
        return "Both Equal";
    }

    private static Double validateScore(Double score, String label) {
        if (score == null || score < 0.0 || score > 100.0) {
            throw new IllegalArgumentException(label + " must be between 0 and 100");
        }
        return score;
    }

    // ---------- Object methods ----------
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student other)) return false;
        return Objects.equals(studentId, other.studentId);   // identity = Student ID
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId);
    }

    @Override
    public String toString() {
        return "Student{id='" + studentId + "', name='" + name + "', email='" + email
                + "', course='" + courseName + "', programming=" + programmingScore
                + ", mathematics=" + mathematicsScore + ", skills=" + technicalSkills + "}";
    }
}
