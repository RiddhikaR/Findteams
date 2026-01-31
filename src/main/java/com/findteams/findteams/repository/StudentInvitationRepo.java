package com.findteams.findteams.repository;

import com.findteams.findteams.model.StudentGroup;
import com.findteams.findteams.model.StudentInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StudentInvitationRepo extends JpaRepository<StudentInvitation, Long> {

    // ✅ Fetch invitations RECEIVED by a specific student
    List<StudentInvitation> findByReceiverId(Long receiverId);

    // ✅ Also add this — prevents duplicate invites to same user for same group
    List<StudentInvitation> findByGroupIdAndReceiverId(Long groupId, Long receiverId);

    List<StudentInvitation> findBySenderId(Long senderId);
    List<StudentInvitation> findByReceiverIdAndStatus(Long receiverId, String status);

}
