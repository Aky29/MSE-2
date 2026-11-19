-- =====================================================================
-- Q2 : Database design - Student Profile and Skill Development System
-- MySQL 8.0.16+ (CHECK constraints are enforced from 8.0.16)
-- =====================================================================
CREATE DATABASE IF NOT EXISTS student_skill_db;
USE student_skill_db;

DROP TABLE IF EXISTS student_skills;
DROP TABLE IF EXISTS students;

-- Requirement 8: duplicate IDs prevented by PRIMARY KEY, duplicate e-mails by UNIQUE,
-- scores restricted to 0..100 by CHECK constraints.
CREATE TABLE students (
    student_id        VARCHAR(20)  NOT NULL,
    name              VARCHAR(100) NOT NULL,
    email             VARCHAR(150) NOT NULL,
    course_name       VARCHAR(100) NOT NULL,
    programming_score DOUBLE       NOT NULL,
    mathematics_score DOUBLE       NOT NULL,
    CONSTRAINT pk_students  PRIMARY KEY (student_id),
    CONSTRAINT uq_email     UNIQUE (email),
    CONSTRAINT chk_prog     CHECK (programming_score BETWEEN 0 AND 100),
    CONSTRAINT chk_math     CHECK (mathematics_score BETWEEN 0 AND 100)
);

-- One row per (student, skill): the "list of technical skills" attribute (1 : N)
CREATE TABLE student_skills (
    student_id VARCHAR(20) NOT NULL,
    skill      VARCHAR(50) NOT NULL,
    CONSTRAINT pk_student_skills PRIMARY KEY (student_id, skill),
    CONSTRAINT fk_skills_student FOREIGN KEY (student_id)
        REFERENCES students(student_id) ON DELETE CASCADE
);

CREATE INDEX idx_students_course ON students(course_name);
