package com.findteams.findteams.repository;

import com.findteams.findteams.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepo extends JpaRepository<Message, Long> {
    List<Message> findByGroupIdOrderByCreatedAtAsc(Long groupId);
}
