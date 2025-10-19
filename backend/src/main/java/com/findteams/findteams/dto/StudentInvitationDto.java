// File: dto/StudentInvitationDTO.java
package com.findteams.findteams.dto;

import lombok.Data;

@Data
public class StudentInvitationDto {
    private Long id;
    private String senderName;
    private String receiverName;
    private String groupName;
    private String purpose;
    private String status;
}
