# Viva preparation – business logic & collections

## Where each collection is used (StudentService)
| Collection | Where | Why |
|---|---|---|
| `List<Student>` | all result lists, `findAll()` | ordered, allows duplicates, easy to sort/stream |
| `List<String>` | `technicalSkills` in `Student` | skills belong to one student, order kept |
| `LinkedHashMap<String,String>` | `normalizeSkills()` | key = lower-case skill → removes duplicates but keeps insertion order |
| `TreeSet<String>` | `getStudentsWithIdenticalSkillSets()` | sorted, duplicate-free; Set equality ignores order, so [Java,SQL] equals [sql,java] |
| `Map<Set<String>, List<Student>>` | same method (`Collectors.groupingBy`) | key = skill set, value = students → groups with size > 1 are matches |
| `TreeMap<String,List<String>>` | `getStudentsBySharedSkill()` | skill → students, sorted by skill name; `computeIfAbsent` |
| `Comparator` | `getMeritList()` | `comparingDouble(avg).reversed().thenComparing(name)` |

## Likely questions
* **Encapsulation?** Private fields, validating setters (score 0–100), derived values computed by methods, not stored.
* **Why isn't average stored in DB?** It is derived data; storing it risks inconsistency when a score changes.
* **How are duplicates prevented?** Service checks `existsById`; DB PRIMARY KEY / UNIQUE(email) is the safety net.
* **Where does validation happen?** 3 layers: Bean Validation on DTOs (400), setter check in `Student`, CHECK constraints in MySQL.
* **Why a separate `student_skills` table?** A student has many skills (1:N); columns can't hold a list (1NF).
* **Why DTOs?** Request shape ≠ entity; update DTO has optional fields.
* **Controller/Service/Repository roles?** HTTP only / business rules / database access.
* **`@RestControllerAdvice`?** Central place converting exceptions to JSON + correct status codes.
* **Merit-list ties?** Same average → same rank (like SQL `RANK()`).
