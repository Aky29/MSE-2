package com.college.studentprofile.controller;

import com.college.studentprofile.dto.MeritListEntry;
import com.college.studentprofile.dto.ScoreSkillUpdateRequest;
import com.college.studentprofile.dto.SkillGroup;
import com.college.studentprofile.dto.StudentRequest;
import com.college.studentprofile.model.Student;
import com.college.studentprofile.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    // ---- Q3 (1) Add a student ----
    @PostMapping
    public ResponseEntity<Student> addStudent(@Valid @RequestBody StudentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addStudent(request));
    }

    // ---- Q3 (2) Retrieve all students ----
    @GetMapping
    public List<Student> getAllStudents() {
        return service.getAllStudents();
    }

    // ---- Q3 (3) Search for a student by ID ----
    @GetMapping("/{id}")
    public Student getStudentById(@PathVariable String id) {
        return service.getById(id);
    }

    // ---- Q3 (4) Update student scores and skills ----
    @PutMapping("/{id}/scores-skills")
    public Student updateScoresAndSkills(@PathVariable String id,
                                         @Valid @RequestBody ScoreSkillUpdateRequest request) {
        return service.updateScoresAndSkills(id, request);
    }

    // ---- Q3 (5) Delete a student record ----
    @DeleteMapping("/{id}")
    public Map<String, String> deleteStudent(@PathVariable String id) {
        service.deleteStudent(id);
        return Map.of("message", "Student " + id + " deleted successfully");
    }

    // ---- Q3 (6) Merit list ----
    @GetMapping("/merit-list")
    public List<MeritListEntry> getMeritList() {
        return service.getMeritList();
    }

    // ---- Extra business-rule endpoints (Q2) ----

    /** Search by id, email and/or course: /api/students/search?email=a@b.com */
    @GetMapping("/search")
    public List<Student> search(@RequestParam(required = false) String id,
                                @RequestParam(required = false) String email,
                                @RequestParam(required = false) String course) {
        return service.search(id, email, course);
    }

    /** Students whose whole skill set is identical. */
    @GetMapping("/skills/identical-sets")
    public List<SkillGroup> identicalSkillSets() {
        return service.getStudentsWithIdenticalSkillSets();
    }

    /** Students grouped by each skill they share. */
    @GetMapping("/skills/shared")
    public List<SkillGroup> sharedSkills() {
        return service.getStudentsBySharedSkill();
    }

    /** Programming needs improvement (< 50) but mathematics >= 75. */
    @GetMapping("/needs-programming-improvement")
    public List<Student> weakProgrammingStrongMaths() {
        return service.getWeakProgrammingStrongMaths();
    }
}
