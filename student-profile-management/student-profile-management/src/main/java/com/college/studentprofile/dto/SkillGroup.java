package com.college.studentprofile.dto;

import java.util.List;

/** A group of students sharing the same skill(s). */
public record SkillGroup(List<String> skills, List<String> students) {
}
