package com.findteams.findteams.controller;

import com.findteams.findteams.dto.ChatMessageDto;
import com.findteams.findteams.model.Message;
import com.findteams.findteams.model.StudentProfileDetails;
import com.findteams.findteams.repository.StudentRepo;
import com.findteams.findteams.service.MessageService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Transactional
public class MessageController {

    private final MessageService messageService;
    private final StudentRepo studentRepo;

    // Get all messages in a group and mark as read for logged-in user
    @GetMapping("/{groupId}")
    public List<ChatMessageDto> getMessages(@PathVariable Long groupId, Principal principal) {
        StudentProfileDetails user = studentRepo.findByUsername(principal.getName());
        Long userId = user.getId();

        // mark all messages as read
        messageService.markMessagesAsRead(groupId, userId);

        List<Message> messages = messageService.getMessagesByGroup(groupId);

        return messages.stream().map(msg -> {
            ChatMessageDto dto = new ChatMessageDto();
            dto.setId(msg.getId());
            dto.setGroupId(msg.getGroupId());
            dto.setSenderId(msg.getSenderId());
            dto.setSenderName(msg.getSenderId() != null
        ? studentRepo.findById(msg.getSenderId()).map(StudentProfileDetails::getName).orElse("Unknown")
        : "Unknown");

// New fields
studentRepo.findById(msg.getSenderId()).ifPresent(student -> {
    dto.setSenderCourse(student.getCourse());
    dto.setSenderCgpa(student.getCgpa());
    dto.setSenderHostellerOrDayscholar(student.getHostellerOrDayscholar());
    dto.setSenderPreferences(student.getPreferences());
    dto.setSenderSummary(student.getResume() != null ? student.getResume().getSummary() : "");
});

            dto.setContent(msg.getContent());
            dto.setCreatedAt(msg.getCreatedAt());
            dto.setRead(msg.isReadForUser(userId));
            return dto;
        }).toList();
    }

    // Get unread message count for logged-in user in a group
    @GetMapping("/unreadCount")
public Map<String, Long> getUnreadCount(Principal principal) {
    StudentProfileDetails user = studentRepo.findByUsername(principal.getName());
    Long userId = user.getId();
    long count = messageService.countUnreadMessages(userId);
    return Map.of("count", count);
}
@GetMapping("/unreadCountPerGroup")
public Map<Long, Long> getUnreadCountPerGroup(Principal principal) {
    StudentProfileDetails user = studentRepo.findByUsername(principal.getName());
    return messageService.getUnreadCountPerGroup(user.getId());
}


}
