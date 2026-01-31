// File: dto/StudentInvitationDTO.java
package com.findteams.findteams.dto;

import java.util.List;

import lombok.Data;

@Data
public class StudentInvitationDto {
    private Long id;
    private String purpose;
    private String status;

    private String senderName;
    private String receiverName;
    private String groupName;

    private Long senderId;
    private Long receiverId;
    private String senderRegNo;
    private String senderCourse;
    private String senderBatch;
    private String senderHostellerOrDayscholar;
    private Double senderCgpa;
    private List<String> senderPreferences;

 private String senderResumeUrl; // URL to fetch resume
private String senderSummary;   // Resume summary

   // new
}
