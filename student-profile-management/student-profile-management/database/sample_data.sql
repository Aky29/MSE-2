USE student_skill_db;

INSERT INTO students VALUES
('S101','Aarav Sharma','aarav@college.edu','B.Tech CSE',92,88),
('S102','Diya Verma','diya@college.edu','B.Tech CSE',78,81),
('S103','Rohan Gupta','rohan@college.edu','B.Tech IT',45,82),   -- weak programming, strong maths
('S104','Meera Nair','meera@college.edu','B.Tech CSE-AI',66,58),
('S105','Kabir Singh','kabir@college.edu','B.Tech IT',40,35),
('S106','Ananya Rao','ananya@college.edu','B.Tech CSE',85,85),
('S107','Ishaan Mehta','ishaan@college.edu','B.Tech CSE-AI',55,79),
('S108','Sana Khan','sana@college.edu','B.Tech IT',72,68);

INSERT INTO student_skills VALUES
('S101','Java'),('S101','SQL'),('S101','Spring Boot'),
('S102','Java'),('S102','SQL'),
('S103','Python'),('S103','Statistics'),
('S104','Python'),('S104','Machine Learning'),
('S105','HTML'),('S105','CSS'),
('S106','Java'),('S106','SQL'),          -- same set as S102
('S107','Python'),('S107','Statistics'), -- same set as S103
('S108','Java'),('S108','React');
