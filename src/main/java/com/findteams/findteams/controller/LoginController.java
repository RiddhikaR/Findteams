package com.findteams.findteams.controller;
import com.findteams.findteams.dto.PreferencesDto;
import java.security.Principal;
import java.util.HashMap;

import java.util.Map;
import java.util.UUID;

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
import com.findteams.findteams.service.MailService;
import com.findteams.findteams.service.ResumeParsingService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/findteams")
@RequiredArgsConstructor
public class LoginController {
    private final StudentRepo studentRepo;
    private final JwtUtil jwtUtil;
    private final StudentResumeRepo resumeRepo;
    private final ResumeParsingService resumeParsingService;
    private final MailService mailService;
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
        response.put("userId",student.getId());
        System.out.println(student.getId());
        response.put("studentName",student.getName());
        System.out.println(student.getName());
    } else {
        response.put("success", false);
        response.put("message", "Incorrect password");
        
    }

    return response;
}
@PostMapping("/forgotPassword")
@Transactional
public Map<String,Object> forgotPassword(@RequestBody Map<String,String> body){
    Map<String,Object> response = new HashMap<>();
    String username = body.get("username");
    StudentProfileDetails student = studentRepo.findByUsername(username);

    if(student == null){
        response.put("success", false);
        response.put("message", "User not found");
        return response;
    }

    String token = UUID.randomUUID().toString();
    student.setResetToken(token);
    student.setResetExpiry(System.currentTimeMillis() + 15*60*1000); // 15 min expiry
    studentRepo.save(student);

    // Send email
    String resetLink = "http://localhost:5173/reset-password?username=" + username + "&token=" + token;
    mailService.sendMail(student.getEmail(), "Password Reset Link", 
        "Click the link to reset your password:\n" + resetLink);

    response.put("success", true);
    response.put("message", "Reset link sent to your email");
    return response;
}
@PostMapping("/resetPassword")
@Transactional
public Map<String,Object> resetPassword(@RequestBody Map<String,String> body){
    Map<String,Object> response = new HashMap<>();
    String username = body.get("username");
    String token = body.get("token");
    String password = body.get("password");

    StudentProfileDetails student = studentRepo.findByUsername(username);
    if(student == null || student.getResetToken() == null){
        response.put("success", false);
        response.put("message", "Invalid request");
        return response;
    }

    if(!student.getResetToken().equals(token) || student.getResetExpiry() < System.currentTimeMillis()){
        response.put("success", false);
        response.put("message", "Invalid or expired token");
        return response;
    }

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    student.setPassword(encoder.encode(password));
    student.setResetToken(null);
    student.setResetExpiry(null);
    studentRepo.save(student);

    response.put("success", true);
    response.put("message", "Password reset successfully");
    return response;
}



    @PostMapping("/uploadResume")
@Transactional
public Map<String, Object> uploadResume(
        @RequestParam("file") MultipartFile file,
        Principal principal) {

    Map<String, Object> response = new HashMap<>();
    try {
        String username = principal.getName();
        StudentProfileDetails student = studentRepo.findByUsername(username);

        if (student == null) {
            response.put("success", false);
            response.put("message", "Student not found");
            return response;
        }

        StudentResume resume = student.getResume();
        if (resume == null) {
            resume = new StudentResume();
            resume.setStudent(student);
            student.setResume(resume); // link both sides
        }

        resume.setData(file.getBytes());
        resumeRepo.saveAndFlush(resume); // save before parsing

        // Call service with correct method
        resumeParsingService.parseAndSaveSummary(resume, file);

        response.put("success", true);
        response.put("message", "Resume uploaded successfully. Parsing will happen in the background.");

    } catch (Exception e) {
        e.printStackTrace();
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


    

