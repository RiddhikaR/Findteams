package com.findteams.findteams.repository;

import com.findteams.findteams.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepo extends JpaRepository<Message, Long> {

    List<Message> findByGroupIdOrderByCreatedAtAsc(Long groupId);

    @Query("""
        SELECT COUNT(m)
        FROM Message m
        WHERE :userId NOT MEMBER OF m.readByUserIds
          AND m.senderId <> :userId
    """)
    long countAllUnreadMessages(@Param("userId") Long userId);

    void deleteByGroupId(Long groupId);

    @Query("""
        SELECT m.groupId, COUNT(m)
        FROM Message m
        WHERE :userId NOT MEMBER OF m.readByUserIds
          AND m.senderId <> :userId
        GROUP BY m.groupId
    """)
    List<Object[]> countUnreadMessagesPerGroup(@Param("userId") Long userId);

    @Query("""
        SELECT COUNT(m)
        FROM Message m
        WHERE m.groupId IN :groupIds
          AND :userId NOT MEMBER OF m.readByUserIds
          AND m.senderId <> :userId
    """)
    long countUnreadMessagesForGroups(@Param("userId") Long userId,
                                      @Param("groupIds") List<Long> groupIds);

    @Query("""
        SELECT m.groupId, COUNT(m)
        FROM Message m
        WHERE m.groupId IN :groupIds
          AND :userId NOT MEMBER OF m.readByUserIds
          AND m.senderId <> :userId
        GROUP BY m.groupId
    """)
    List<Object[]> countUnreadMessagesPerGroupLimited(@Param("userId") Long userId,
                                                      @Param("groupIds") List<Long> groupIds);
}
