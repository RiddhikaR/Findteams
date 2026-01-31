package com.findteams.findteams.controller;

import com.findteams.findteams.model.StudentProfileDetails;
import com.findteams.findteams.service.StudentSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StudentSearchController {

    private final StudentSearchService studentSearchService;

    @GetMapping("/searchStudents")
    public List<Map<String, Object>> searchStudents(
            @RequestParam(required = false) String cgpa,
            @RequestParam(required = false) String batch,
            @RequestParam(required = false) String course,
            @RequestParam(required = false) String hostellerOrDayscholar,
            @RequestParam(required = false) List<String> preferences,
            @RequestParam(required = false) String prompt
    ) {
        return studentSearchService.searchStudentsCombined(cgpa, batch, course, hostellerOrDayscholar, preferences, prompt);
    }
}
