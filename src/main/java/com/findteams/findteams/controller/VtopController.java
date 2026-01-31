package com.findteams.findteams.controller;

import com.findteams.findteams.model.StudentProfileDetails;
import com.findteams.findteams.repository.StudentRepo;
import com.findteams.findteams.scraper.VtopScrapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import java.util.*;



/**
 * Controller for handling VTOP login and student profile scraping.
 * Provides endpoints to log in to VTOP, handle CAPTCHA if required, 
 * scrape student profile details, and store them in the database.
*/

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/vtop")
@RequiredArgsConstructor
public class VtopController {

    
    private final StudentRepo studentRepo;
    
    private String savedUsername;
    private String savedPassword;
    private Map<String, VtopScrapper> activeScrapers = new HashMap<>();


    /**
     * Login endpoint for VTOP.
     *
     * @param body A map containing "username", "password", and optionally "captcha".
     * @return A map containing:
     *         - "captcha_required" (boolean)
     *         - "captcha_src" (String, if CAPTCHA is required)
     *         - "success" (boolean, login status)
     *         - "message" (String, descriptive message)
     */

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body) {
        Map<String, Object> response = new HashMap<>();

        String username = body.get("username");
        String password = body.get("password");
        String captcha = body.get("captcha");

        if (captcha == null || captcha.isEmpty()) {
            
            
            VtopScrapper scraper = new VtopScrapper();
            activeScrapers.put(username, scraper);
            String captchaSrc = scraper.loadLoginPageAndFillCredentials(username, password);
            
            if (captchaSrc != null) {
                response.put("captcha_required", true);
                response.put("captcha_src", captchaSrc);
                response.put("message", "CAPTCHA required");
            } else {
                
                
                boolean success = scraper.submitCaptchaAndLogin(null);
                String error = scraper.checkLoginError();   // 🔹 check for error after login attempt

        if (error != null) {
            response.put("success", false);
            response.put("message", error);  // send error string to frontend
            return response;
        }
                
                if(success){
                    StudentProfileDetails student=studentRepo.findByUsername(username);
                    if(student==null){
                        StudentProfileDetails scrapedDetails=scraper.goToStudentProfile();
                        scrapedDetails.setUsername(username);
                        studentRepo.save(scrapedDetails);
                        student = scrapedDetails;
                    }
                     response.put("userId", student.getId());
        response.put("studentName", student.getName());
                }
                response.put("captcha_required", false);
                response.put("success", success);
                response.put("message", success ? "Login successful" : "Login failed");
            }
        } else {
            VtopScrapper scraper = activeScrapers.get(username);
    if (scraper == null) {
        response.put("success", false);
        response.put("message", "Session expired. Please try again.");
        return response;
    }

           
            boolean success = scraper.submitCaptchaAndLogin(captcha);
            String error = scraper.checkLoginError();  // ✅ check here also

if (error != null) {
    response.put("success", false);
    response.put("message", error);
    return response;
}
            if(success){
                    StudentProfileDetails student=studentRepo.findByUsername(username);
                    if(student==null){
                        StudentProfileDetails scrapedDetails=scraper.goToStudentProfile();
                        scrapedDetails.setUsername(username);
                        studentRepo.save(scrapedDetails);
                         student = scrapedDetails;
                    }
                     
                    response.put("userId", student.getId());
    response.put("studentName", student.getName());
            }
            response.put("captcha_required", false);
            response.put("success", success);
            response.put("message", success ? "Login successful" : "Login failed");
        }
        

        return response;
    }
    @GetMapping("/check-user")
    @Transactional
public Map<String, Object> checkUser(@RequestParam String username) {
    Map<String, Object> response = new HashMap<>();
    boolean exists = studentRepo.findByUsername(username) != null;
    response.put("exists", exists);
    return response;
}


    
    
}



