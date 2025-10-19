package com.findteams.findteams.controller;
import com.findteams.findteams.dto.PreferencesDto;
import java.security.Principal;
import java.util.HashMap;

import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.findteams.findteams.model.StudentProfileDetails;
import com.findteams.findteams.model.StudentResume;
import com.findteams.findteams.repository.StudentRepo;
import com.findteams.findteams.repository.StudentResumeRepo;
import com.findteams.findteams.security.JwtUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/findteams")
@RequiredArgsConstructor
public class LoginController {
    private final StudentRepo studentRepo;
    private final JwtUtil jwtUtil;
    private final StudentResumeRepo resumeRepo;
    
    @PostMapping("setPassword")
    public Map<String,Object> setPassword(@RequestBody Map<String,String> body){
        Map <String,Object> response=new HashMap<>();
        String username=body.get("username");
        String password=body.get("password");
        StudentProfileDetails student=studentRepo.findByUsername(username);
        if(student!=null){
            if(student.getPassword()==null){
                BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
                student.setPassword(encoder.encode(password));
                studentRepo.save(student);
                String token=jwtUtil.generateToken(username);
                response.put("success",true);
                response.put("message","password set sucessfully");
                response.put("token",token);
            }
            else{
                response.put("success",false);
                response.put("message","You have already set the password please login");
            }
        }
        


        return response;
    }
    @PostMapping("/login")
    @Transactional
public Map<String, Object> login(@RequestBody Map<String, String> body) {
    Map<String, Object> response = new HashMap<>();
    String username = body.get("username");
    String password = body.get("password");

    StudentProfileDetails student = studentRepo.findByUsername(username);
    if (student == null) {
        response.put("success", false);
        response.put("message", "You have not created your profile yet.Please sign up!");
        return response;
    }

    if (student.getPassword() == null) {
        response.put("success", false);
        response.put("message", "You have not created your profile yet.Please sign up!");
        return response;
    }

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    if (encoder.matches(password, student.getPassword())) {
        String token = jwtUtil.generateToken(username);
        response.put("success", true);
        response.put("message", "Login successful");
        response.put("token", token);
    } else {
        response.put("success", false);
        response.put("message", "Incorrect password");
    }

    return response;
}


    @PostMapping("/uploadResume")
    public Map<String, Object> uploadResume(
            @RequestParam("file") MultipartFile file,
            Principal principal) {
        Map<String, Object> response = new HashMap<>();
        try {
            String username = principal.getName();
            StudentProfileDetails student = studentRepo.findByUsername(username);

            StudentResume resume = student.getResume();
            if (resume == null) {
                resume = new StudentResume();
                resume.setStudent(student);
            }
            resume.setData(file.getBytes());
            resumeRepo.save(resume);

            response.put("success", true);
            response.put("message", "Resume uploaded successfully");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error uploading resume: " + e.getMessage());
        }
        return response;
    }

    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
    @PostMapping("/setPreferences")
    @Transactional
    public Map<String,Object> setPreferences(@RequestBody PreferencesDto preferences,Principal principal){
        Map <String,Object> response=new HashMap<>();
        
        StudentProfileDetails studentProfileDetails=studentRepo.findByUsername(principal.getName());
        studentProfileDetails.setPreferences(preferences.getPreferences());
        studentRepo.save(studentProfileDetails);
        response.put("message","preferences has been set successfully");
        
        return response;
    }
}


    

