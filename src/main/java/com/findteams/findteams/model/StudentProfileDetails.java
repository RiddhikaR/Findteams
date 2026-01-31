package com.findteams.findteams.model;

import java.util.List;


import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Student details scraped from VTOP(regNo,name,email,course,hostellerOrDayscholor,cgpa) are stored in this table
 * Username is entered by the student during inital login to set up their profile which is stored in the this table
 * Password is set by student after VTOP login occurs to login into this website which is stored in this table
 * Resume is uploaded by the student which is stored in this table
 * Student selects their preferences from dropdown which is stored in this table as a list

 */

@Entity
@Table(name="student")
@Data
@RequiredArgsConstructor
public class StudentProfileDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String regNo;
    private String name;
    
    private String email;
    private String course;
    private String hostellerOrDayscholar;
    private String cgpa;
    private String resetToken;
private Long resetExpiry; // timestamp

    
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> preferences;
    
     @OneToOne(mappedBy="student", cascade=CascadeType.ALL)
    private StudentResume resume;

    
}
