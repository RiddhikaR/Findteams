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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class MessageController {

    private final MessageService messageService;
    private final StudentRepo studentRepo;
    

    // Send a message to a group
    @PostMapping("/send")
    @Transactional
    public Map<String, Object> sendMessage(@RequestBody Map<String, String> body,
                                           Principal principal) {
        Map<String, Object> response = new HashMap<>();
        try {
            String username = principal.getName();
            StudentProfileDetails sender = studentRepo.findByUsername(username);

            Long groupId = Long.parseLong(body.get("groupId"));
            String content = body.get("content");

            Message message = new Message();
            message.setGroupId(groupId);
            message.setSenderId(sender.getId());
            message.setContent(content);

            messageService.sendMessage(message);

            response.put("success", true);
            response.put("message", "Message sent");
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return response;
    }

    @GetMapping("/{groupId}")
public List<ChatMessageDto> getMessages(@PathVariable Long groupId) {
    List<Message> messages = messageService.getMessagesByGroup(groupId);

    return messages.stream().map(msg -> {
        ChatMessageDto dto = new ChatMessageDto();
        dto.setId(msg.getId());
        dto.setGroupId(msg.getGroupId());
        dto.setSenderId(msg.getSenderId());
        dto.setSenderName(studentRepo.findById(msg.getSenderId())
                          .map(s -> s.getName())  // You can use getUsername() if needed
                          .orElse("Unknown"));
        dto.setContent(msg.getContent());
        dto.setCreatedAt(msg.getCreatedAt());
        return dto;
    }).toList();
}

}
