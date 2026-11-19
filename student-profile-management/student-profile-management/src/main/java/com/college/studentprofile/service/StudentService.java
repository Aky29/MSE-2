package com.college.studentprofile.service;

import com.college.studentprofile.dto.MeritListEntry;
import com.college.studentprofile.dto.ScoreSkillUpdateRequest;
import com.college.studentprofile.dto.SkillGroup;
import com.college.studentprofile.dto.StudentRequest;
import com.college.studentprofile.exception.DuplicateStudentException;
import com.college.studentprofile.exception.StudentNotFoundException;
import com.college.studentprofile.model.Student;
import com.college.studentprofile.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * All business logic lives here.
 * Collections used: List (results, skills), Set/TreeSet (order-independent skill sets),
 * Map/LinkedHashMap/TreeMap (grouping, de-duplication), Comparator (sorting).
 */
@Service
public class StudentService {

    /** Mathematics score needed for the "weak programming, strong maths" report. */
    public static final double MATH_STRONG_THRESHOLD = 75.0;

    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    // ===================== CRUD =====================

    @Transactional
    public Student addStudent(StudentRequest req) {
        String id = req.studentId().trim();
        String email = req.email().trim();

        // Business rule: prevent duplicate student IDs (and e-mails)
        if (repository.existsById(id)) {
            throw new DuplicateStudentException("Student ID already exists: " + id);
        }
        if (repository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateStudentException("Email already registered: " + email);
        }

        Student student = new Student(id, req.name().trim(), email, req.courseName().trim(),
                req.programmingScore(), req.mathematicsScore(), normalizeSkills(req.technicalSkills()));
        return repository.save(student);
    }

    @Transactional(readOnly = true)
    public List<Student> getAllStudents() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Student getById(String id) {
        return repository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
    }

    @Transactional
    public Student updateScoresAndSkills(String id, ScoreSkillUpdateRequest req) {
        Student student = getById(id);
        if (req.programmingScore() != null) student.setProgrammingScore(req.programmingScore());
        if (req.mathematicsScore() != null) student.setMathematicsScore(req.mathematicsScore());
        if (req.technicalSkills() != null) student.setTechnicalSkills(normalizeSkills(req.technicalSkills()));
        return repository.save(student);
    }

    @Transactional
    public void deleteStudent(String id) {
        if (!repository.existsById(id)) {
            throw new StudentNotFoundException(id);
        }
        repository.deleteById(id);
    }

    // ===================== Search =====================

    /** Search by ID, email or course (any combination; at least one required). */
    @Transactional(readOnly = true)
    public List<Student> search(String id, String email, String course) {
        id = blankToNull(id);
        email = blankToNull(email);
        course = blankToNull(course);
        if (id == null && email == null && course == null) {
            throw new IllegalArgumentException("Provide at least one of: id, email, course");
        }
        return repository.search(id, email, course);
    }

    // ===================== Merit list =====================

    /** Students sorted by average score (descending). Equal averages share a rank. */
    @Transactional(readOnly = true)
    public List<MeritListEntry> getMeritList() {
        List<Student> sorted = repository.findAll().stream()
                .sorted(Comparator.comparingDouble(Student::getAverageScore).reversed()
                        .thenComparing(Student::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();

        List<MeritListEntry> merit = new ArrayList<>();
        int rank = 0;
        double previousAvg = Double.NaN;
        for (int i = 0; i < sorted.size(); i++) {
            Student s = sorted.get(i);
            if (Double.compare(s.getAverageScore(), previousAvg) != 0) {
                rank = i + 1;                       // new rank only when average changes
                previousAvg = s.getAverageScore();
            }
            merit.add(new MeritListEntry(rank, s.getStudentId(), s.getName(), s.getCourseName(),
                    s.getAverageScore(), s.getPerformanceCategory()));
        }
        return merit;
    }

    // ===================== Same technical skills =====================

    /**
     * Groups students whose COMPLETE skill set is identical (case-insensitive, order ignored).
     * Key = TreeSet of lower-case skills; Set equality makes [java, sql] == [SQL, Java].
     */
    @Transactional(readOnly = true)
    public List<SkillGroup> getStudentsWithIdenticalSkillSets() {
        Map<Set<String>, List<Student>> groups = repository.findAll().stream()
                .filter(s -> !s.getTechnicalSkills().isEmpty())
                .collect(Collectors.groupingBy(
                        s -> s.getTechnicalSkills().stream()
                                .map(String::toLowerCase)
                                .collect(Collectors.toCollection(TreeSet::new)),
                        LinkedHashMap::new,
                        Collectors.toList()));

        return groups.entrySet().stream()
                .filter(e -> e.getValue().size() > 1)             // only real matches
                .map(e -> new SkillGroup(new ArrayList<>(e.getKey()),
                        e.getValue().stream().map(this::label).toList()))
                .toList();
    }

    /** For each individual skill, the students who have it (only skills held by 2+ students). */
    @Transactional(readOnly = true)
    public List<SkillGroup> getStudentsBySharedSkill() {
        Map<String, List<String>> bySkill = new TreeMap<>();      // sorted by skill name
        for (Student s : repository.findAll()) {
            for (String skill : s.getTechnicalSkills()) {
                bySkill.computeIfAbsent(skill.toLowerCase(), k -> new ArrayList<>()).add(label(s));
            }
        }
        return bySkill.entrySet().stream()
                .filter(e -> e.getValue().size() > 1)
                .map(e -> new SkillGroup(List.of(e.getKey()), e.getValue()))
                .toList();
    }

    // ===================== Needs improvement in programming =====================

    /**
     * Programming is in the "Needs Improvement" band (< 50) but mathematics >= 75.
     */
    @Transactional(readOnly = true)
    public List<Student> getWeakProgrammingStrongMaths() {
        return repository.findByProgrammingScoreLessThanAndMathematicsScoreGreaterThanEqual(
                Student.SATISFACTORY_MIN, MATH_STRONG_THRESHOLD);
    }

    // ===================== Helpers =====================

    /** Trim, drop blanks, remove case-insensitive duplicates, keep original order. */
    private List<String> normalizeSkills(List<String> skills) {
        if (skills == null) return new ArrayList<>();
        Map<String, String> unique = new LinkedHashMap<>();
        for (String skill : skills) {
            if (skill == null || skill.isBlank()) continue;
            String trimmed = skill.trim();
            unique.putIfAbsent(trimmed.toLowerCase(), trimmed);
        }
        return new ArrayList<>(unique.values());
    }

    private String label(Student s) {
        return s.getStudentId() + " - " + s.getName();
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
