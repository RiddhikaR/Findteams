package com.findteams.findteams.controller;



import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.findteams.findteams.dto.ChatMessageDto;
import com.findteams.findteams.model.Message;
import com.findteams.findteams.service.MessageService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final MessageService messageService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    // WebSocket endpoint
    @MessageMapping("/chat/sendMessage")
public void sendMessageViaWebSocket(ChatMessageDto dto) {
    // Convert DTO to entity
    Message message = new Message();
    message.setGroupId(dto.getGroupId());
    message.setSenderId(dto.getSenderId());
    message.setContent(dto.getContent());
    
    // Save to DB
    Message savedMessage = messageService.sendMessage(message);

    // Prepare DTO to broadcast (with saved ID, timestamp, etc.)
    ChatMessageDto broadcastDto = new ChatMessageDto();
    broadcastDto.setId(savedMessage.getId());
    broadcastDto.setGroupId(savedMessage.getGroupId());
    broadcastDto.setSenderId(savedMessage.getSenderId());
    broadcastDto.setSenderName(dto.getSenderName()); // optionally fetch from DB if needed
    broadcastDto.setContent(savedMessage.getContent());
    broadcastDto.setCreatedAt(savedMessage.getCreatedAt());

    // Broadcast to all subscribers of the group
    simpMessagingTemplate.convertAndSend("/topic/group/" + savedMessage.getGroupId(), broadcastDto);
}



}
