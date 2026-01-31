package com.findteams.findteams.service;

import com.findteams.findteams.model.StudentGroup;
import com.findteams.findteams.model.StudentInvitation;
import com.findteams.findteams.repository.StudentGroupRepo;
import com.findteams.findteams.repository.StudentInvitationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;


@Service
@RequiredArgsConstructor
public class StudentInvitationService {

    private final StudentInvitationRepo invitationRepo;
    private final StudentGroupRepo groupRepo;

    // ✅ Send Invite (Prevents Duplicate + Only Owner Has Permission + Can't Invite Self)
    public StudentInvitation sendInvitation(Long senderId, Long receiverId, Long groupId) {

        StudentGroup group = groupRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // ✅ Only the group owner can send invites
        if (!group.getOwnerId().equals(senderId)) {
            throw new RuntimeException("Only the group owner can send invitations.");
        }

        // ✅ Prevent owner inviting themselves
        if (senderId.equals(receiverId)) {
            throw new RuntimeException("You cannot invite yourself.");
        }

        // ✅ Check if invitation already exists (Pending or Accepted)
        List<StudentInvitation> existingInvites =
                invitationRepo.findByReceiverId(receiverId).stream()
                        .filter(inv -> inv.getGroupId().equals(groupId)
                                && (inv.getStatus().equals("PENDING") || inv.getStatus().equals("ACCEPTED")))
                        .toList();

        if (!existingInvites.isEmpty()) {
            throw new RuntimeException("Invitation already sent to this user for this group.");
        }

        // ✅ Create new invitation
        StudentInvitation invitation = new StudentInvitation();
        invitation.setSenderId(senderId);
        invitation.setReceiverId(receiverId);
        invitation.setGroupId(groupId);
        invitation.setPurpose(group.getPurpose());
        invitation.setStatus("PENDING");

        return invitationRepo.save(invitation);
    }
    //sender
   
    // ✅ Accept Invitation
    public StudentInvitation acceptInvitation(Long invitationId) {
    StudentInvitation invitation = invitationRepo.findById(invitationId)
            .orElseThrow(() -> new RuntimeException("Invitation not found"));

    invitation.setStatus("ACCEPTED");

    StudentGroup group = groupRepo.findById(invitation.getGroupId())
            .orElseThrow(() -> new RuntimeException("Group not found"));

    // Determine which student to add
    Long studentToAdd = "REQUEST_JOIN_GROUP".equals(invitation.getPurpose())
            ? invitation.getSenderId()  // requester
            : invitation.getReceiverId(); // invited student

    if (!group.getMemberIds().contains(studentToAdd)) {
        group.getMemberIds().add(studentToAdd);
        groupRepo.save(group);
    }

    return invitationRepo.save(invitation);
}


    // ✅ Reject Invitation
    public StudentInvitation rejectInvitation(Long invitationId) {
        StudentInvitation invitation = invitationRepo.findById(invitationId)
                .orElseThrow(() -> new RuntimeException("Invitation not found"));

        invitation.setStatus("REJECTED");
        return invitationRepo.save(invitation);
    }

    // ✅ Get Invitations for a Student
    public List<StudentInvitation> getInvitationsForStudent(Long studentId) {
    return invitationRepo.findByReceiverIdAndStatus(studentId, "PENDING");
}

    // ✅ Request to join a public group
public StudentInvitation requestToJoinPublicGroup(Long studentId, Long groupId) {

    StudentGroup group = groupRepo.findById(groupId)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Group not found"));

    if (!group.isPublicGroup()) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "This is not a public group");
    }

    // 🤫 SILENT ignore for own group
    if (group.getOwnerId().equals(studentId)) {
        throw new ResponseStatusException(HttpStatus.NO_CONTENT);
    }

    // already member
    if (group.getMemberIds().contains(studentId)) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "You are already a member of this group");
    }

    // already requested
    boolean alreadyRequested = invitationRepo
            .findBySenderId(studentId)
            .stream()
            .anyMatch(inv -> inv.getGroupId().equals(groupId));

    if (alreadyRequested) {
        throw new ResponseStatusException(
                HttpStatus.CONFLICT, "Request already sent");
    }

    StudentInvitation invitation = new StudentInvitation();
    invitation.setSenderId(studentId);
    invitation.setReceiverId(group.getOwnerId());
    invitation.setGroupId(groupId);
    invitation.setPurpose("REQUEST_JOIN_GROUP");
    invitation.setStatus("PENDING");

    return invitationRepo.save(invitation);
}



}
