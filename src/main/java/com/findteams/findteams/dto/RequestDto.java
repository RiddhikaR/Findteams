package com.findteams.findteams.dto;

import java.util.List;

import lombok.Data;

@Data
public class RequestDto {
    
    private String cgpa;
    private String course;
    private String hostellerOrDayscholar;
    private String year;
    private List<String> preferences;

}
