-- =====================================================================
-- Q2 : SQL for each requirement
-- =====================================================================
USE student_skill_db;

-- 1. Average score of each student
SELECT student_id, name,
       ROUND((programming_score + mathematics_score) / 2, 2) AS average_score
FROM students;

-- 2. Performance category
SELECT student_id, name,
       ROUND((programming_score + mathematics_score) / 2, 2) AS average_score,
       CASE
           WHEN ROUND((programming_score + mathematics_score) / 2, 2) >= 85 THEN 'Excellent'
           WHEN ROUND((programming_score + mathematics_score) / 2, 2) >= 70 THEN 'Good'
           WHEN ROUND((programming_score + mathematics_score) / 2, 2) >= 50 THEN 'Satisfactory'
           ELSE 'Needs Improvement'
       END AS performance_category
FROM students;

-- 3. Stronger subject
SELECT student_id, name, programming_score, mathematics_score,
       CASE
           WHEN programming_score > mathematics_score THEN 'Programming'
           WHEN programming_score < mathematics_score THEN 'Mathematics'
           ELSE 'Both Equal'
       END AS stronger_subject
FROM students;

-- 4. Search by ID, email or course
SELECT * FROM students WHERE student_id = 'S101';
SELECT * FROM students WHERE email = 'diya@college.edu';
SELECT * FROM students WHERE course_name LIKE '%CSE%';

-- 5. Merit list (average descending, ties share a rank)
SELECT RANK() OVER (ORDER BY (programming_score + mathematics_score) / 2 DESC) AS merit_rank,
       student_id, name, course_name,
       ROUND((programming_score + mathematics_score) / 2, 2) AS average_score
FROM students
ORDER BY average_score DESC, name;

-- 6a. Students with an IDENTICAL set of skills
SELECT skill_set, GROUP_CONCAT(student_id ORDER BY student_id) AS students
FROM (
    SELECT student_id, GROUP_CONCAT(LOWER(skill) ORDER BY LOWER(skill)) AS skill_set
    FROM student_skills
    GROUP BY student_id
) AS per_student
GROUP BY skill_set
HAVING COUNT(*) > 1;

-- 6b. Students sharing any individual skill
SELECT LOWER(skill) AS skill, GROUP_CONCAT(student_id ORDER BY student_id) AS students
FROM student_skills
GROUP BY LOWER(skill)
HAVING COUNT(*) > 1;

-- 7. Programming needs improvement (< 50) but mathematics >= 75
SELECT student_id, name, programming_score, mathematics_score
FROM students
WHERE programming_score < 50 AND mathematics_score >= 75;

-- 8. Constraint demo (each statement below should FAIL)
-- INSERT INTO students VALUES ('S101','Dup','dup@x.com','CSE',50,50);   -- duplicate ID
-- INSERT INTO students VALUES ('S999','Bad','bad@x.com','CSE',150,50);  -- score > 100
-- INSERT INTO students VALUES ('S998','Bad','bad2@x.com','CSE',50,-5);  -- score < 0
