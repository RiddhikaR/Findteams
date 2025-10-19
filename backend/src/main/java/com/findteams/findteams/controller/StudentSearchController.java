package com.findteams.findteams.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.findteams.findteams.model.StudentProfileDetails;
import com.findteams.findteams.repository.StudentRepo;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Transactional
public class StudentSearchController {

    private final StudentRepo studentRepo;

    @GetMapping("/api/searchStudents")
public List<StudentProfileDetails> searchStudents(
        @RequestParam(required = false) String cgpa,
        @RequestParam(required = false) String batch,
        @RequestParam(required = false) String course,
        @RequestParam(required = false) String hostellerOrDayscholar,
        @RequestParam(required = false) List<String> preferences
) {
    return studentRepo.searchStudents(cgpa, batch, course, hostellerOrDayscholar, preferences);
}

}
