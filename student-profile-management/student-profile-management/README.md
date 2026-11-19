# Student Profile and Skill Development Management System
CS336B – Object Oriented Programming Using Java · MSE-II (Spring Boot + MySQL)

## Tech stack
Java 17 · Spring Boot 3.3.5 · Spring Data JPA (Hibernate) · Bean Validation · MySQL 8 · Maven

## Project structure
```
database/            schema.sql, sample_data.sql, queries.sql   (Q2)
src/main/java/com/college/studentprofile/
  model/Student.java               Q1 – encapsulated class + JPA entity
  dto/                             request/response records
  repository/StudentRepository     Spring Data JPA queries
  service/StudentService           business rules + collections
  controller/StudentController     Q3 – REST APIs
  exception/                       custom exceptions + global handler
AI_PROMPTS.md                      deliverable 2 (edit with YOUR prompts)
VIVA_NOTES.md                      deliverable 3 preparation
```

## How to run
1. Install JDK 17+, Maven 3.9+, MySQL 8.
2. (Optional) run `database/schema.sql` then `database/sample_data.sql` in MySQL.
   If skipped, Hibernate creates the DB/tables automatically (`ddl-auto=update`).
3. Set credentials (or edit `application.properties`):
   `export DB_USER=root DB_PASSWORD=yourpassword`  (Windows: `set DB_PASSWORD=...`)
4. `mvn spring-boot:run`  → http://localhost:8080
5. Unit tests: `mvn test`

## REST API
| Method | URL | Purpose |
|---|---|---|
| POST | `/api/students` | Add a student |
| GET | `/api/students` | All students |
| GET | `/api/students/{id}` | Search by ID |
| PUT | `/api/students/{id}/scores-skills` | Update scores and/or skills |
| DELETE | `/api/students/{id}` | Delete |
| GET | `/api/students/merit-list` | Merit list (average ↓) |
| GET | `/api/students/search?id=&email=&course=` | Search by ID / email / course |
| GET | `/api/students/skills/identical-sets` | Students with identical skill sets |
| GET | `/api/students/skills/shared` | Students grouped by shared skill |
| GET | `/api/students/needs-programming-improvement` | Programming < 50 and Maths ≥ 75 |

Every student JSON also contains the derived fields `averageScore`, `performanceCategory`, `strongerSubject`.

### Examples
```bash
curl -X POST localhost:8080/api/students -H "Content-Type: application/json" -d '{
  "studentId":"S201","name":"Neha Jain","email":"neha@college.edu","courseName":"B.Tech CSE",
  "programmingScore":88,"mathematicsScore":79,"technicalSkills":["Java","SQL"]}'

curl -X PUT localhost:8080/api/students/S201/scores-skills -H "Content-Type: application/json" \
  -d '{"programmingScore":91,"technicalSkills":["Java","SQL","Spring Boot"]}'

curl "localhost:8080/api/students/search?course=cse"
curl localhost:8080/api/students/merit-list
```
Errors: `400` validation (score outside 0–100, bad e-mail, missing field) · `404` unknown ID · `409` duplicate ID/e-mail.

## Business rules & assumptions
* Average = (programming + mathematics) / 2, rounded to 2 decimals.
* Categories: ≥85 Excellent · 70–84.99 Good · 50–69.99 Satisfactory · <50 Needs Improvement.
* "Needs improvement in programming" = programming score < 50 (the lowest band), with mathematics ≥ 75.
* "Same technical skills": two views – identical complete skill set, and any individual skill shared. Comparison is case-insensitive.
* Merit list: equal averages share a rank; ties are listed alphabetically.
* Duplicate IDs are rejected (service check + primary key); e-mail is also unique.
* Skills are trimmed, blank entries dropped, case-insensitive duplicates removed.
