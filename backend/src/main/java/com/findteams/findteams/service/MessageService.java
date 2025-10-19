package com.findteams.findteams.service;

import com.findteams.findteams.model.Message;
import com.findteams.findteams.repository.MessageRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepo messageRepo;

    public Message sendMessage(Message message) {
        return messageRepo.save(message);
    }

    public List<Message> getMessagesByGroup(Long groupId) {
        return messageRepo.findByGroupIdOrderByCreatedAtAsc(groupId);
    }
}
