# Three AI Prompts with Brief Reflections

> NOTE: These are drafts matching how this project was built. Replace them with the
> prompts you actually used (and your own honest reflections) before submitting.

## Prompt 1 – Design
**Prompt:** "Design a Spring Boot project for a Student Profile and Skill Development system using
MySQL. Students have ID, name, email, course, programming and maths scores and a list of skills.
Suggest entities, tables, and layers."
**Reflection:** The AI suggested separating skills into their own table (1:N) instead of a
comma-separated column, and keeping average/category as computed methods. I understood this is
First Normal Form and avoids stale derived data.

## Prompt 2 – Business logic with collections
**Prompt:** "Write Java service methods to group students who have identical skill sets
(case-insensitive, order ignored), build a merit list with ranks, and find students with
programming below 50 but maths at least 75."
**Reflection:** Using a `TreeSet` of lower-case skills as a `Map` key was a neat way to ignore
order. The AI's first answer compared lists, which fails when order differs, so I verified with
test data. "Needs improvement in programming" was ambiguous; I chose < 50 and documented it.

## Prompt 3 – Validation and error handling
**Prompt:** "Add validation so scores are 0-100, student IDs are unique, and return proper HTTP
status codes with JSON error messages."
**Reflection:** Validation works best in layers: DTO annotations, entity setters and DB CHECK
constraints. I learned how `@RestControllerAdvice` maps exceptions to 400/404/409. Limitation:
AI-generated code was not compiled for me, so I tested every endpoint myself.
