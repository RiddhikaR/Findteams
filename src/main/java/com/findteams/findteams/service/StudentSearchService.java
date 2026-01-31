package com.findteams.findteams.service;

import com.findteams.findteams.model.StudentProfileDetails;
import com.findteams.findteams.model.StudentResume;
import com.findteams.findteams.repository.StudentRepo;
import com.findteams.findteams.repository.StudentResumeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentSearchService {

    private final StudentRepo studentRepo;
    private final ResumeMatchingService resumeMatchingService;
    private final StudentResumeRepo resumeRepo;

    public List<Map<String, Object>> searchStudentsCombined(
            String cgpa,
            String batch,
            String course,
            String hostellerOrDayscholar,
            List<String> preferences,
            String prompt
    ) {

        // Step 1: Get normal filtered students
        List<StudentProfileDetails> students = studentRepo.searchStudents(
                cgpa, batch, course, hostellerOrDayscholar, preferences
        );

        List<Map<String, Object>> promptMatches = new ArrayList<>();
        if (prompt != null && !prompt.isBlank()) {
            promptMatches = resumeMatchingService.matchResumesFromPrompt(prompt);

            // Filter students to only those that appear in promptMatches
            Set<Long> matchedIds = promptMatches.stream()
                    .map(pm -> (Long) pm.get("id"))
                    .collect(Collectors.toSet());

            students = students.stream()
                    .filter(s -> matchedIds.contains(s.getId()))
                    .collect(Collectors.toList());
        }

        // Step 2: Build combined result with summary
        List<Map<String, Object>> combinedResults = new ArrayList<>();

        for (StudentProfileDetails s : students) {
            Map<String, Object> studentData = new HashMap<>();
            studentData.put("id", s.getId());
            studentData.put("name", s.getName());
            studentData.put("regNo", s.getRegNo());
            studentData.put("cgpa", s.getCgpa());
            studentData.put("course", s.getCourse());
            studentData.put("hostellerOrDayscholar", s.getHostellerOrDayscholar());
            studentData.put("preferences", s.getPreferences());

            // Get summary from promptMatches if available, else from resumeRepo
            String summary = null;
            Optional<Map<String, Object>> match = promptMatches.stream()
                    .filter(pm -> Objects.equals(pm.get("id"), s.getId()))
                    .findFirst();

            if (match.isPresent()) {
                summary = (String) match.get().get("summary");
            } else {
                Optional<StudentResume> resume = resumeRepo.findByStudentId(s.getId());
                if (resume.isPresent()) summary = resume.get().getSummary();
            }

            studentData.put("summary", summary);

            combinedResults.add(studentData);
        }

        return combinedResults;
    }
}
