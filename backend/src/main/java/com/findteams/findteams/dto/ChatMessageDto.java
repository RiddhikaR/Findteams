
package com.findteams.findteams.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ChatMessageDto {
    private Long id;
    private Long groupId;
    private Long senderId;
    private String senderName;  // 👈 This is what you'll show in frontend
    private String content;
    private LocalDateTime createdAt;
}
