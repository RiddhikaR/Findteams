package com.findteams.findteams.service;

import com.findteams.findteams.model.Message;
import com.findteams.findteams.repository.MessageRepo;
import com.findteams.findteams.repository.StudentGroupRepo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepo messageRepo;
    private final StudentGroupRepo groupRepo;
    // Send a message
    public Message sendMessage(Message message) {
        return messageRepo.save(message);
    }

    // Get all messages for a group
    public List<Message> getMessagesByGroup(Long groupId) {
        return messageRepo.findByGroupIdOrderByCreatedAtAsc(groupId);
    }

    // Mark messages as read for a user in a group
    public void markMessagesAsRead(Long groupId, Long userId) {
        List<Message> messages = messageRepo.findByGroupIdOrderByCreatedAtAsc(groupId);
        for (Message m : messages) {
            if (!m.getReadByUserIds().contains(userId)) {
                m.getReadByUserIds().add(userId);
            }
        }
        messageRepo.saveAll(messages);
    }

    // Count unread messages for a user in a specific group (efficient)
    public long countUnreadMessages(Long userId) {
    List<Long> groupIds = groupRepo.findGroupIdsByMemberId(userId);
    if (groupIds.isEmpty()) return 0L;
    return messageRepo.countUnreadMessagesForGroups(userId, groupIds);
}



public Map<Long, Long> getUnreadCountPerGroup(Long userId) {
    // ✅ Step 1: Get only active group IDs
    List<Long> groupIds = groupRepo.findGroupIdsByMemberId(userId);
    if (groupIds.isEmpty()) return Map.of();

    // ✅ Step 2: Query only for those groups
    List<Object[]> results = messageRepo.countUnreadMessagesPerGroupLimited(userId, groupIds);

    // ✅ Step 3: Convert to Map
    return results.stream()
            .collect(Collectors.toMap(
                r -> (Long) r[0], // groupId
                r -> (Long) r[1]  // count
            ));
}



}
