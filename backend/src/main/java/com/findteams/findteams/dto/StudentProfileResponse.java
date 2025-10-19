package com.findteams.findteams.dto;

import java.util.List;

import lombok.Data;

@Data
public class StudentProfileResponse {
    private long id;
    private String name;
    private String course;
    private String email;
    private String cgpa;
    private String hostellerOrDayscholar;
    private String regNo;
    private List<String> preferences;
    private boolean success;
}
