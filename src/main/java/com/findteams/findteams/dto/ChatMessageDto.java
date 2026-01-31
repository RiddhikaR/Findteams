package com.findteams.findteams.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ChatMessageDto {
    private Long id;
    private Long groupId;
    private Long senderId;
    private String senderName;

    // Add these fields
    private String senderCourse;
    private String senderCgpa;
    private String senderHostellerOrDayscholar;
    private List<String> senderPreferences;
    private String senderSummary;

    private String content;
    private boolean read;
    private LocalDateTime createdAt;
}
