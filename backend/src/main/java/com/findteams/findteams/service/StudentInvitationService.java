package com.findteams.findteams.service;

import com.findteams.findteams.model.StudentGroup;
import com.findteams.findteams.model.StudentInvitation;
import com.findteams.findteams.repository.StudentGroupRepo;
import com.findteams.findteams.repository.StudentInvitationRepo;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentInvitationService {

    private final StudentInvitationRepo invitationRepo;
    private final StudentGroupRepo groupRepo;

    public StudentInvitation sendInvitation(Long senderId, Long receiverId, Long groupId) {
    StudentGroup group = groupRepo.findById(groupId)
        .orElseThrow(() -> new RuntimeException("Group not found"));

    StudentInvitation invitation = new StudentInvitation();
    invitation.setSenderId(senderId);
    invitation.setReceiverId(receiverId);
    invitation.setGroupId(groupId);
    invitation.setPurpose(group.getPurpose());  // <-- set group's purpose here
    invitation.setStatus("PENDING");
    return invitationRepo.save(invitation);
}



    public StudentInvitation acceptInvitation(Long invitationId) {
        StudentInvitation invitation = invitationRepo.findById(invitationId)
                .orElseThrow(() -> new RuntimeException("Invitation not found"));

        invitation.setStatus("ACCEPTED");

        StudentGroup group = groupRepo.findById(invitation.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group not found"));

        

        group.getMemberIds().add(invitation.getReceiverId());
        groupRepo.save(group);

        return invitationRepo.save(invitation);
    }

    public StudentInvitation rejectInvitation(Long invitationId) {
        StudentInvitation invitation = invitationRepo.findById(invitationId)
                .orElseThrow(() -> new RuntimeException("Invitation not found"));
        invitation.setStatus("REJECTED");
        return invitationRepo.save(invitation);
    }
    public List<StudentInvitation> getInvitationsForStudent(Long studentId) {
    return invitationRepo.findByReceiverId(studentId);
}

}
