package com.findteams.findteams.dto;

import java.util.List;

import lombok.Data;

@Data
public class ParsedResumeDataDto {
    private List<String> skills;
    private String summary;

    private int overall;
    private int measurableImpact;
    private int skillCoverage;
    
    private int growthAndLearning;
}
