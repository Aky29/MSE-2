package com.college.studentprofile.repository;

import com.college.studentprofile.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, String> {

    boolean existsByEmailIgnoreCase(String email);

    /** Search by ID, email and/or course. A null parameter is ignored. */
    @Query("""
           SELECT s FROM Student s
           WHERE (:id IS NULL OR s.studentId = :id)
             AND (:email IS NULL OR LOWER(s.email) = LOWER(:email))
             AND (:course IS NULL OR LOWER(s.courseName) LIKE LOWER(CONCAT('%', :course, '%')))
           """)
    List<Student> search(@Param("id") String id,
                         @Param("email") String email,
                         @Param("course") String course);

    /** programming < limit AND mathematics >= limit */
    List<Student> findByProgrammingScoreLessThanAndMathematicsScoreGreaterThanEqual(
            Double programmingBelow, Double mathematicsAtLeast);
}
