package com.findteams.findteams.service;

import com.findteams.findteams.model.StudentResume;
import com.findteams.findteams.repository.StudentResumeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResumeMatchingService {

    private final StudentResumeRepo resumeRepo;

    private static final Set<String> STOPWORDS = Set.of(
            "in","for","of","on","and","a","an","to","with","by","from","as","the","is","at","that","this","it","be","or"
    );

    @Transactional
    public List<Map<String, Object>> matchResumesFromPrompt(String promptText) {
        if (promptText == null || promptText.isBlank()) return Collections.emptyList();

        // 1️⃣ Extract skills from prompt
        List<String> promptSkills = Arrays.stream(promptText.toLowerCase().split("[^a-z0-9]+"))
                .filter(s -> !STOPWORDS.contains(s) && !s.isBlank())
                .collect(Collectors.toList());

        List<StudentResume> resumes = resumeRepo.findAllWithStudent();
        List<Map<String, Object>> results = new ArrayList<>();

        for (StudentResume resume : resumes) {
    if (resume.getStudent() == null) continue;

    String summary = resume.getSummary() != null ? resume.getSummary().toLowerCase() : "";

    double matchScore = 0;
    for (String skill : promptSkills) {
        if (summary.contains(skill)) matchScore += 1.0;
    }

    if (matchScore == 0) continue; // <-- skip students with no match

    double scaledScore = Math.min(100, matchScore * 10);

    Map<String, Object> data = new HashMap<>();
    data.put("id", resume.getStudent().getId());
    data.put("name", resume.getStudent().getName());
    data.put("regNo", resume.getStudent().getRegNo());
    data.put("cgpa", resume.getStudent().getCgpa());
    data.put("course", resume.getStudent().getCourse());
    data.put("summary", resume.getSummary());
    data.put("matchScore", scaledScore);

    results.add(data);
}


        // 4️⃣ Sort by matchScore descending
        results.sort((a, b) -> Double.compare((double) b.get("matchScore"), (double) a.get("matchScore")));

        return results;
    }
}
