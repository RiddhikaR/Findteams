package com.findteams.findteams.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.findteams.findteams.dto.StudentProfileResponse;
import com.findteams.findteams.model.StudentProfileDetails;
import com.findteams.findteams.model.StudentResume;
import com.findteams.findteams.repository.StudentRepo;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class StudentProfile {
    
    private final StudentRepo studentRepo;

    @Transactional
    @GetMapping("getProfile")
    public StudentProfileResponse getProfile( Principal principal){
        String username=principal.getName();
        
        StudentProfileDetails student=studentRepo.findByUsername(username);
        StudentProfileResponse studentProfileResponse=new StudentProfileResponse();
        if(student!=null){
            
            studentProfileResponse.setId(student.getId());
            studentProfileResponse.setCgpa(student.getCgpa());
            studentProfileResponse.setRegNo(student.getRegNo());
            studentProfileResponse.setCourse(student.getCourse());
            studentProfileResponse.setEmail(student.getEmail());
            studentProfileResponse.setHostellerOrDayscholar(student.getHostellerOrDayscholar());
            studentProfileResponse.setName(student.getName());
            studentProfileResponse.setSuccess(true);
            studentProfileResponse.setPreferences(student.getPreferences());
            return studentProfileResponse;


        }
        else{
            studentProfileResponse.setSuccess(false);
            return studentProfileResponse;
        }
        
    }
    @GetMapping("/resume")
    @Transactional
public ResponseEntity<byte[]> getResume(Principal principal) {
    String username = principal.getName();
    StudentProfileDetails student = studentRepo.findByUsername(username);

    StudentResume resume = student.getResume();
    if (resume == null) {
        return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=resume.pdf")
            .contentType(MediaType.APPLICATION_PDF) 
            .body(resume.getData());
}
@GetMapping("/resume/{studentId}")
@Transactional
public ResponseEntity<byte[]> getResumeById(@PathVariable Long studentId) {
    StudentProfileDetails student = studentRepo.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found"));

    StudentResume resume = student.getResume();
    if (resume == null || resume.getData() == null) {
        return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=resume_" + student.getRegNo() + ".pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(resume.getData());
}

}
